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

            <v-container fluid style="margin: 0px; padding: 0px" >
              <v-layout row align-center>

                <v-flex xs1 >
                  <v-tooltip right >
                    <span>Unique ID used by Chi and other applications to identify your experiment. Keep it concise.</span>
                    <v-icon
                      slot="activator">help_outline</v-icon>
                  </v-tooltip>
                </v-flex>
                <v-flex xs11>
                  <v-text-field
                    style="width: 90%"
                    v-model="experimentId"
                    label="Experiment ID"
                    :rules="experimentIdRules"
                  ></v-text-field>
                </v-flex>

              </v-layout>
              <v-layout row align-center>

                <v-flex offset-xs1>
                  <v-text-field
                    style="width: 300px"
                    v-model="experimentIdSlug"
                    label="Final Experiment ID"
                    :readonly="true"
                    :disabled="true"
                  ></v-text-field>
                </v-flex>

              </v-layout>
            </v-container>

            <experiment-desc-editing v-model="descriptions"/>

            <v-container fluid style="margin: 0px; padding: 0px">

              <v-layout row align-top>

                <v-flex xs1>
                  <v-tooltip right>
                    <span>Disable <b>reporting</b> if don't want this experiment to be measured by Chi.</span>
                    <v-icon
                      slot="activator">help_outline</v-icon>
                  </v-tooltip>
                </v-flex>
                <v-flex xs11>
                  <v-switch
                    label="Reporting" v-model="reportingEnabled" slot="activator">
                  </v-switch>
                </v-flex>

              </v-layout>
              <v-layout row align-center>

                <v-flex offset-xs1>
                  <v-select
                    v-bind:items="deviceClasses"
                    v-model="selectedDeviceClass"
                    label="Device class"
                    item-value="text"
                  ></v-select>
                </v-flex>

              </v-layout>
              <v-layout row align-center>

                <v-flex offset-xs1>
                  <v-select
                    label="Variants"
                    :rules="variantsRules"
                    chips
                    append-icon=""
                    tags
                    v-model="variantNames">
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
                </v-flex>

              </v-layout>
              <v-layout row align-center>

                <v-flex offset-xs1>
                  <v-slider
                    v-model="percentPerVariant"
                    thumb-label
                    :label="parseInt(this.percentPerVariant) + '% per variant'"
                    step="1"
                    v-bind:max="maxPercentPerVariant"
                    v-bind:min="1"
                    ticks></v-slider>
                </v-flex>

              </v-layout>
              <v-layout row align-center>

                <v-flex xs1>
                  <v-tooltip right>
                    <span><b>Internal</b> means &mdash; available only in Allegro intranet.</span>
                    <v-icon
                      slot="activator">help_outline</v-icon>
                  </v-tooltip>
                </v-flex>
                <v-flex xs11>
                  <v-text-field
                    v-model="selectedInternal"
                    label="Internal variant"
                  ></v-text-field>
                </v-flex>

              </v-layout>
            </v-container>

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
  import ExperimentDescEditing from './experiment/ExperimentDescEditing'

  import _ from 'lodash'

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
        reportingEnabled: true,
        selectedInternal: '',
        variantNames: [baseVariant],
        percentPerVariant: 1,
        deviceClasses: ['all', 'phone', 'desktop', 'tablet'],
        selectedDeviceClass: 'all',
        sendingDataToServer: false,
        errors: [],
        descriptions: {}
      }
    },

    computed: {
      maxPercentPerVariant () {
        return 100 / this.variantNames.length
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
        return _.map(this.variantNames, v => this.slugify(v))
      }
    },

    components: {
      ChiPanel,
      ExperimentDescEditing
    },

    methods: {
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
        return {
          id: this.experimentIdSlug,
          description: this.descriptions.description,
          documentLink: this.descriptions.documentLink,
          groups: this.descriptions.groups,
          reportingEnabled: this.reportingEnabled,
          variantNames: this.slugifiedVariants,
          internalVariantName: this.selectedInternal !== '' ? this.selectedInternal : null,
          deviceClass: this.selectedDeviceClass !== 'all' ? this.selectedDeviceClass : null,
          percentage: this.percentPerVariant
        }
      },

      removeVariant (variant) {
        this.remove('variantNames', variant)
      },

      remove (arrayName, toRemove) {
        this[arrayName].splice(this[arrayName].indexOf(toRemove), 1)
        this[arrayName] = [...this[arrayName]]
      },

      ...mapActions(['createExperiment', 'getExperiments'])

    }
  }
</script>
