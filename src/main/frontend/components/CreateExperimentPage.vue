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

            <v-container fluid style="margin: 0px; padding: 0px" text-xs-center>
              <v-layout row align-center>

                <v-flex xs1>
                  <v-tooltip right >
                    <span>Unique ID used by Chi and other applications to identify your experiment. Keep it concise.</span>
                    <v-icon
                      slot="activator">help_outline</v-icon>
                  </v-tooltip>
                </v-flex>
                <v-flex xs11>
                  <v-text-field
                    id="experimentIdFormField"
                    v-model="experimentId"
                    label="Experiment ID"
                    :rules="experimentIdRules"
                  ></v-text-field>
                </v-flex>

              </v-layout>
              <v-layout row align-center>

                <v-flex offset-xs1>
                  <v-text-field
                    id="experimentIdSlug"
                    style="width: 300px"
                    v-model="experimentIdSlug"
                    label="Final Experiment ID"
                    :readonly="true"
                    :disabled="true"
                  ></v-text-field>
                </v-flex>

              </v-layout>
            </v-container>

            <experiment-desc-editing ref="experimentDescEditing"
                                     v-model="descriptions"/>

            <experiment-custom-parameter-editing ref="experimentCustomParamEditing"
                                                 v-model="customParameter" />

            <experiment-variants-editing v-model="variants"
                                         ref="experimentVariantsEditing"
                                         :allowModifyRegularVariants="true"/>

            <v-container fluid style="margin: 0px; padding: 0px" text-xs-center>
              <v-layout row align-top>

                <v-flex xs1>
                  <v-tooltip right close-delay="1000">
                    <span>
                      If you are going to experiment using Opbox and you want to filter
                      users' interactions by defining NGA events &mdash;
                      pick FRONTEND.
                      <br/>
                      If you are going to experiment using GTM &mdash; pick GTM.
                      Otherwise, pick BACKEND.
                      <br/>
                      <a target="_blank" style="color: aqua" href="https://rtd.allegrogroup.com/docs/chi/reporting/">
                      Read more in χ Docs</a>
                    </span>
                    <v-icon
                      slot="activator">help_outline</v-icon>
                  </v-tooltip>
                </v-flex>
                <v-flex xs11 id="reportingTypeDropdown">
                  <v-select
                    :items="availableReportingTypes"
                    v-model="reportingType"
                    label="Reporting type"
                  ></v-select>
                </v-flex>

              </v-layout>
            </v-container>

            <v-container fluid style="margin: 0px; padding: 0px" text-xs-center v-if="reportingType === 'FRONTEND'">
              <v-layout row align-top>

                <v-flex xs1>
                  <v-tooltip right close-delay="1000">
                    <span>
                      Chi will use specified NGA events to filter user interactions.
                      <br/><br/>
                      For power users only.
                      <br/><br/>
                      <a target="_blank" style="color: aqua" href="https://rtd.allegrogroup.com/docs/chi/reporting/#raportowanie-frontendowe">
                      Read more in χ Docs</a>
                    </span>

                    <v-icon
                      slot="activator">help_outline</v-icon>
                  </v-tooltip>
                </v-flex>
                <v-flex xs11 text-xs-left>
                  <experiment-event-filters
                    v-on:eventDefinitionsChanged="onEventDefinitionsChanged"
                    :read-only="false">
                  </experiment-event-filters>
                </v-flex>

              </v-layout>
            </v-container>

            <v-btn
              id="createExperimentFormSubmitButton"
              @click="onSubmit"
              color="success">create</v-btn>
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
  import ExperimentVariantsEditing from './experiment/ExperimentVariantsEditing'
  import ExperimentEventFilters from './experiment/ExperimentEventFilters'
  import { slugify } from '../utils/slugify'
  import _ from 'lodash'

  import ChiPanel from './ChiPanel.vue'
  import ExperimentCustomParameterEditing from './experiment/ExperimentCustomParameterEditing'

  export default {
    mounted () {
      this.getExperiments()
    },

    data () {
      const baseVariant = 'base'
      return {
        createFormValid: true,
        experimentIdRules: [
          (v) => !!v || 'Experiment ID is required',
          (v) => this.isExperimentIdUnique(v) || 'Experiment ID must be unique.'
        ],
        baseVariant: baseVariant,
        experimentId: '',
        reportingEnabled: true,
        sendingDataToServer: false,
        errors: [],
        descriptions: null,
        customParameter: {},
        variants: null,
        reportingType: 'BACKEND',
        availableReportingTypes: ['BACKEND', 'FRONTEND', 'GTM'],
        eventDefinitions: []
      }
    },

    computed: {
      showForm () {
        return !this.sendingDataToServer
      },

      showProgress () {
        return this.sendingDataToServer
      },

      experimentIdSlug () {
        return slugify(this.experimentId)
      },

      slugifiedVariants () {
        return _.map(this.variants.variantNames, v => slugify(v))
      }
    },

    components: {
      ChiPanel,
      ExperimentDescEditing,
      ExperimentVariantsEditing,
      ExperimentEventFilters,
      ExperimentCustomParameterEditing
    },

    methods: {
      onEventDefinitionsChanged (newEventDefinitions) {
        this.eventDefinitions = JSON.parse(JSON.stringify(newEventDefinitions))
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

      validate () {
        // this.$refs.experimentCustomParamEditing.validate()
        const descValid = this.$refs.experimentDescEditing.validate()
        const variantsValid = this.$refs.experimentVariantsEditing.validate()

        return this.$refs.createForm.validate() && descValid && variantsValid
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

      isExperimentIdUnique () {
        return _.find(this.$store.state.experiments.experiments, e => e.id === this.experimentIdSlug) === undefined
      },

      getExperimentDataToSend () {
        let experimentCreationRequest = {
          id: this.experimentIdSlug,
          description: this.descriptions.description,
          documentLink: this.descriptions.documentLink,
          customParameterName: this.customParameter.name,
          customParameterValue: this.customParameter.value,
          groups: this.descriptions.groups,
          reportingEnabled: this.reportingEnabled,
          variantNames: this.slugifiedVariants,
          internalVariantName: this.variants.internalVariantName !== '' ? this.variants.slugifiedInternalVariantName : null,
          deviceClass: this.variants.deviceClass !== 'all' ? this.variants.deviceClass : null,
          percentage: this.variants.percentage,
          reportingType: this.reportingType,
          eventDefinitions: this.eventDefinitions
        }

        return experimentCreationRequest
      },
      ...mapActions([
        'createExperiment',
        'getExperiments'
      ])
    }
  }
</script>
