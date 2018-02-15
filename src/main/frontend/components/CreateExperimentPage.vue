<template>
  <v-container>
    <v-layout>
      <v-flex md12 lg10 xl10 offset-xl1 offset-md1 offset-lg1>

        <chi-panel v-if="showForm" title="New experiment">

          <v-alert v-for="error in errors"
                   color="error" icon="warning" value="true" :key="error">
            {{ error }}
          </v-alert>

          <v-form v-model="createFormValid"
                ref="createForm"
                lazy-validation>

            <v-tooltip bottom>
              <v-text-field
                v-model="experimentId"
                label="Experiment ID"
                :rules="experimentIdRules"
                slot="activator"
              ></v-text-field>
              <span>Unique ID used by Chi and other applications to identify your experiment. Keep it concise.</span>
            </v-tooltip>

            <v-text-field
              v-model="experimentIdSlug"
              label="Final Experiment ID"
              :readonly="true"
              :disabled="true"
            ></v-text-field>

            <v-tooltip bottom>
              <v-text-field
                v-model="description"
                label="Description"
                slot="activator"
              ></v-text-field>
              <span>Describe shortly what you are going to test.</span>
            </v-tooltip>

            <v-text-field
              v-model="documentLink"
              :rules="documentLinkRules"
              label="Documentation link"
            ></v-text-field>

            <v-tooltip bottom>
              <v-select
                label="Authorization groups"
                slot="activator"
                chips
                append-icon=""
                tags
                v-model="groups">
                <template slot="selection" slot-scope="data">
                  <v-chip
                    close
                    @input="removeGroup(data.item)"
                    :selected="data.selected">
                    <strong>{{ data.item }}</strong>&nbsp;
                  </v-chip>
                </template>
              </v-select>
              <span>Let your team manage this experiment.
                Provide a group name, for example: <code>Tech Content Team</code>.
                <br/>
                Hit <b>ENTER</b> to add a group.</span>
                You can add more than one group.
                <br/>
                Check your groups on your JIRA profile &mdash; https://jira.allegrogroup.com/secure/ViewProfile.jspa
            </v-tooltip>

            <v-tooltip bottom>
              <v-switch label="Reporting" v-model="reportingEnabled" slot="activator">
              </v-switch>
              <span>Disable <b>reporting</b> if don't want this experiment to be measured by Chi.</span>
            </v-tooltip>

            <v-select
              v-bind:items="deviceClasses"
              v-model="selectedDeviceClass"
              label="Device class"
              item-value="text"
            ></v-select>

            <v-select
              label="Variants"
              :rules="variantsRules"
              chips
              append-icon=""
              tags
              v-model="variants">
              <template slot="selection" slot-scope="data">

                <v-chip
                  v-if="data.item === baseVariant"
                  :selected="data.selected"
                  :disabled="true">
                  <strong>{{ data.item }}</strong>&nbsp;
                </v-chip>

                <v-chip
                  v-else
                  close
                  @input="removeVariant(data.item)"
                  :selected="data.selected">
                  <strong>{{ slugify(data.item) }}</strong>&nbsp;
                </v-chip>

              </template>
            </v-select>

            <v-slider
              v-model="percentPerVariant"
              thumb-label
              :label="parseInt(this.percentPerVariant) + '% per variant'"
              step="1"
              v-bind:max="maxPercentPerVariant"
              v-bind:min="1"
              ticks></v-slider>

            <v-tooltip bottom>
              <v-text-field
                slot="activator"
                v-model="selectedInternal"
                label="Internal"
              ></v-text-field>

              <span>You can choose one of the existing variants to be forced internally
                    or provide additional,
                    internal only variant.
                 <br/>
                    <b>Internal</b> means &mdash; available only in Allegro intranet.</span>
            </v-tooltip>

            <v-btn @click="onSubmit" color="success">create</v-btn>
          </v-form>
        </chi-panel>
        <v-progress-linear v-if="showProgress" v-bind:indeterminate="true"></v-progress-linear>

      </v-flex>
    </v-layout>
  </v-container>
</template>

<script>
  import { mapActions } from 'vuex'
  import _ from 'lodash'
  import {isUri} from 'valid-url'

  import ChiPanel from './ChiPanel.vue'

  export default {
    mounted () {
      this.getExperiments()
    },

    data () {
      const baseVariant = 'base'

      return {
        createFormValid: true,
        variantsRules: [
          (v) => this.variantsUnique() || 'Slugified variant names must be unique.',
          (v) => this.slugifiedVariants.indexOf('') === -1 || 'Slugified variant name can not be empty.',
          (v) => this.noOfVariants() > 1 || 'No variants. Seriously?'
        ],
        experimentIdRules: [
          (v) => !!v || 'Experiment ID is required',
          (v) => this.isExperimentIdUnique(v) || 'Experiment ID must be unique.'
        ],
        baseVariant: baseVariant,
        experimentId: '',
        description: '',
        documentLink: '',
        documentLinkRules: [
          (v) => this.isUrlValid(v) || 'Provided string is not a valid URL'
        ],
        groups: [],
        reportingEnabled: true,
        selectedInternal: '',
        variants: [baseVariant],
        percentPerVariant: 1,
        deviceClasses: ['all', 'smartphone', 'desktop', 'tablet'],
        selectedDeviceClass: 'all',
        sendingDataToServer: false,
        errors: []
      }
    },

    computed: {
      maxPercentPerVariant () {
        return 100 / this.variants.length
      },

      showForm () {
        return !this.sendingDataToServer
      },

      showProgress () {
        return this.sendingDataToServer
      },

      experimentIdSlug () {
        return this.slugify(this.experimentId)
      },

      slugifiedVariants () {
        return _.map(this.variants, v => this.slugify(v))
      }
    },

    components: {
      ChiPanel
    },

    methods: {
      isUrlValid (value) {
        return isUri(value) !== undefined || value === ''
      },

      slugify (str) {
        return str.toString().toLowerCase()
          .replace(/\s+/g, '-')
          .replace(/[^\w-]+/g, '')
          .replace(/--+/g, '-')
          .replace(/^-+/, '')
          .replace(/-+$/, '')
      },

      onSubmit () {
        this.sending()
        this.cleanErrors()

        if (this.$refs.createForm.validate()) {
          this.createExperiment({data: this.getExperimentDataToSend()}).then(response => {
            this.notSending()
            this.$router.push('/experiments/' + this.experimentIdSlug)
          }).catch(error => {
            if (error.response.status === 401) {
              this.setPermissionsError()
            } else {
              this.setUnknownError()
            }
            this.notSending()
          })
        } else {
          this.notSending()
        }
      },

      setPermissionsError () {
        this.errors = ['You have no permission to create experiment.']
      },

      setUnknownError () {
        this.errors = ['Unknown error. Please, contact administrator.']
      },

      cleanErrors () {
        this.errors = []
      },

      sending () {
        this.sendingDataToServer = true
      },

      notSending () {
        this.sendingDataToServer = false
      },

      noOfVariants () {
        return this.slugifiedVariants.length + (this.selectedInternal !== '' ? 1 : 0)
      },

      variantsUnique () {
        let distinctVariants = new Set(this.slugifiedVariants)
        return (this.slugifiedVariants.length === distinctVariants.size)
      },

      isExperimentIdUnique () {
        return _.find(this.$store.state.experiments.experiments, e => e.id === this.experimentIdSlug) === undefined
      },

      getExperimentDataToSend () {
        let result = {
          id: this.experimentIdSlug,
          description: this.description,
          documentLink: this.documentLink,
          groups: this.groups,
          reportingEnabled: this.reportingEnabled,
          variants: this.getVariantsDataToSend()
        }

        if (this.shouldAppendSeparateInternalVariant()) {
          result.variants.push({
            name: this.selectedInternal,
            predicates: [this.getInternalPredicateDataToSend()]
          })
        }

        return result
      },

      shouldAppendSeparateInternalVariant () {
        return this.selectedInternal !== ''
      },

      getVariantsDataToSend () {
        let bounds = this.calculatePercentageBoundsPerVariantForHashPredicate()

        return _.map(this.slugifiedVariants, v => {
          let predicates = [this.getHashPredicateDataToSend(bounds[v])]

          if (this.selectedDeviceClass !== 'all') {
            predicates.push(this.getDevicePredicateDataToSend())
          }

          return {
            name: v,
            predicates: predicates
          }
        })
      },

      calculatePercentageBoundsPerVariantForHashPredicate () {
        let step = this.maxPercentPerVariant
        let result = {}
        _.forEach(_.range(0, this.slugifiedVariants.length), i => {
          result[this.slugifiedVariants[i]] = [parseInt(i * step), parseInt(i * step) + this.percentPerVariant]
        })
        return result
      },

      getHashPredicateDataToSend (bounds) {
        return {
          type: 'HASH',
          from: bounds[0],
          to: bounds[1]
        }
      },

      getDevicePredicateDataToSend () {
        return {
          type: 'DEVICE_CLASS',
          device: this.selectedDeviceClass
        }
      },

      getInternalPredicateDataToSend () {
        return {
          type: 'INTERNAL'
        }
      },

      removeVariant (variant) {
        this.remove('variants', variant)
      },

      removeGroup (group) {
        this.remove('groups', group)
      },

      remove (arrayName, toRemove) {
        this[arrayName].splice(this[arrayName].indexOf(toRemove), 1)
        this[arrayName] = [...this[arrayName]]
      },

      ...mapActions(['createExperiment', 'getExperiments'])

    }
  }
</script>
