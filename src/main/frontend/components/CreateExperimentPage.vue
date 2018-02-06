<template>
  <v-container>
    <v-layout>
      <v-flex md12 lg10 offset-xl1 xl10>

        <chi-panel v-if="showForm" title="New experiment">

          <v-alert v-for="error in errors" color="error" icon="warning" value="true">
            {{ error }}
          </v-alert>

          <form>

            <v-text-field
              v-model="experimentId"
              label="Experiment ID"
              required
            ></v-text-field>

            <v-text-field
              v-model="experimentIdSlug"
              label="Final Experiment ID"
              :readonly="true"
              :disabled="true"
            ></v-text-field>

            <v-text-field
              v-model="description"
              label="Description"
            ></v-text-field>

            <v-select
              label="Authorization groups"
              chips
              append-icon=""
              tags
              v-model="groups"
            >
              <template slot="selection" slot-scope="data">
                <v-chip
                  close
                  @input="removeGroup(data.item)"
                  :selected="data.selected"
                >
                  <strong>{{ data.item }}</strong>&nbsp;
                </v-chip>
              </template>
            </v-select>

            <v-switch label="Reporting" v-model="reportingEnabled"></v-switch>

            <v-select
              v-bind:items="deviceClasses"
              v-model="selectedDeviceClass"
              label="Device class"
              item-value="text"
            ></v-select>

            <v-select
              label="Variants"
              chips
              append-icon=""
              tags
              v-model="variants"
            >
              <template slot="selection" slot-scope="data">

                <v-chip
                  v-if="data.item === baseVariant"
                  :selected="data.selected"
                  :disabled="true"
                >
                  <strong>{{ data.item }}</strong>&nbsp;
                </v-chip>

                <v-chip
                  v-else
                  close
                  @input="removeVariant(data.item)"
                  :selected="data.selected"
                >
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

              <span>You can choose from variants above or provide additional internal variant.</span>
            </v-tooltip>

            <v-btn @click="onSubmit" color="success">create</v-btn>
          </form>
        </chi-panel>
        <v-progress-linear v-if="showProgress" v-bind:indeterminate="true"></v-progress-linear>

      </v-flex>
    </v-layout>
  </v-container>
</template>

<script>
  import { mapActions } from 'vuex'
  import _ from 'lodash'

  import ChiPanel from './ChiPanel.vue'

  export default {
    mounted () {
      this.getExperiments()
    },

    data () {
      const baseVariant = 'base'

      return {
        baseVariant: baseVariant,
        experimentId: '',
        description: '',
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

        if (this.validate()) {
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

      validate () {
        if (this.experimentId === '') {
          this.errors.push('ID is required.')
        }

        if (this.isExperimentIdUnique()) {
          this.errors.push('ID must be unique.')
        }

        let distinctVariants = new Set(this.slugifiedVariants)
        if (this.slugifiedVariants.length !== distinctVariants.size) {
          this.errors.push('Variant names must be unique.')
        }

        if (this.slugifiedVariants.indexOf('') !== -1) {
          this.errors.push('Variant name cant be empty string.')
        }

        return this.errors.length === 0
      },

      isExperimentIdUnique () {
        return _.find(this.$store.state.experiments.experiments, e => e.id === this.experimentIdSlug) !== undefined
      },

      getExperimentDataToSend () {
        let result = {
          id: this.experimentIdSlug,
          description: this.description,
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
        return this.selectedInternal !== '' && !_.find(this.variants, v => this.selectedInternal === v)
      },

      getVariantsDataToSend () {
        let bounds = this.calculatePercentageBoundsPerVariantForHashPredicate()

        return _.map(this.slugifiedVariants, v => {
          let predicates = [this.getHashPredicateDataToSend(bounds[v])]

          if (this.selectedDeviceClass !== 'all') {
            predicates.push(this.getDevicePredicateDataToSend())
          }
          if (this.selectedInternal === v) {
            predicates.push(this.getInternalPredicateDataToSend())
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
