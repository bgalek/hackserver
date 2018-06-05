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
                    style="width: 300px"
                    v-model="experimentIdSlug"
                    label="Final Experiment ID"
                    :readonly="true"
                    :disabled="true"
                  ></v-text-field>
                </v-flex>

              </v-layout>
            </v-container>

            <experiment-desc-editing v-model="descriptions" />

            <v-container fluid style="margin: 0px; padding: 0px" text-xs-center>

              <v-layout row align-top v-if="false">

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
            </v-container>

            <experiment-variants-editing :allowModifyRegularVariants="true" v-model="variants" />

            <v-container fluid style="margin: 0px; padding: 0px" text-xs-center>
              <v-layout row align-top>

                <v-flex xs1>
                  <v-tooltip right>
                    <span>
                      If you are going to experiment using Opbox and you want to filter
                      users' interactions by defining NGA events &mdash;
                      pick FRONTEND.
                      <br/>
                      If you are going to experiment using GTM &mdash; pick GTM.
                      Otherwise, pick BACKEND.
                      <br/>
                      You can find detailed info here: https://rtd.allegrogroup.com/docs/chi/pl/latest/reporting/
                    </span>
                    <v-icon
                      slot="activator">help_outline</v-icon>
                  </v-tooltip>
                </v-flex>
                <v-flex xs11>
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
                  <v-tooltip right>
                    <span>Chi will use specified NGA events to filter user interactions. You can find detailed info here: https://rtd.allegrogroup.com/docs/chi/pl/latest/reporting/</span>
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

            <v-container
              fluid style="margin: 0px; padding: 0px" text-xs-center>
              <v-layout row align-top>

                <v-flex xs1>
                  <v-tooltip right>
                    <span>You can create a group of mutually exclusive experiments. Experiments in group will not interfere with each other. Useful when you have many experiments in the same area of the system.</span>
                    <v-icon
                      slot="activator">help_outline</v-icon>
                  </v-tooltip>
                </v-flex>

                <v-flex xs11 text-xs-left>
                  <v-switch
                    label="Group together with another experiment"
                    v-model="experimentGroup.createGroupFlag"
                  ></v-switch>
                  <v-select
                    v-if="experimentGroup.createGroupFlag"
                    id="groupWithExperiment"
                    :items="experimentsThatCanBeGrouped"
                    v-model="experimentGroup.groupWithExperiment"
                    label="Group with"
                    :rules="groupWithExperimentRules"
                    :autocomplete="true"
                    single-line
                  ></v-select>
                  <v-text-field
                    v-if="experimentGroup.createGroupFlag"
                    id="experimentGroupName"
                    v-model="experimentGroup.experimentGroupName"
                    label="Group name"
                    :rules="experimentGroupIdRules"
                  ></v-text-field>
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
  import { mapState, mapActions } from 'vuex'
  import ExperimentDescEditing from './experiment/ExperimentDescEditing'
  import ExperimentVariantsEditing from './experiment/ExperimentVariantsEditing'
  import ExperimentEventFilters from './experiment/ExperimentEventFilters'
  import { slugify } from '../utils/slugify'
  import _ from 'lodash'

  import ChiPanel from './ChiPanel.vue'

  export default {
    mounted () {
      this.getExperiments()
      this.getExperimentGroups()
    },

    data () {
      const baseVariant = 'base'
      return {
        createFormValid: true,
        experimentIdRules: [
          (v) => !!v || 'Experiment ID is required',
          (v) => this.isExperimentIdUnique(v) || 'Experiment ID must be unique.'
        ],
        experimentGroupIdRules: [
          (v) => this.isExperimentGroupIdUnique(v) || 'Experiment group ID must be unique.',
          (v) => !!v || 'Experiment group ID is required'
        ],
        groupWithExperimentRules: [
          (v) => !!v || 'Pick experiment you want to group together with new experiment.'
        ],
        baseVariant: baseVariant,
        experimentId: '',
        reportingEnabled: true,
        sendingDataToServer: false,
        errors: [],
        descriptions: {},
        variants: {},
        reportingType: 'BACKEND',
        availableReportingTypes: ['BACKEND', 'FRONTEND', 'GTM'],
        eventDefinitions: [],
        experimentGroup: {
          groupWithExperiment: null,
          experimentGroupName: null,
          createGroupFlag: false
        }
      }
    },

    computed: {
      ...mapState({
        experimentsThatCanBeGrouped: state => state.experiments.experiments
          .filter((e, index, array) => { return e.canBeGrouped() })
          .map((e, index, array) => { return e.id })
          .sort()
      }),

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
      ExperimentEventFilters
    },

    methods: {
      onEventDefinitionsChanged (newEventDefinitions) {
        this.eventDefinitions = JSON.parse(JSON.stringify(newEventDefinitions))
      },

      onSubmit () {
        this.sending()
        this.cleanErrors()

        if (this.$refs.createForm.validate()) {
          let createExperimentMethod = this.shouldBeGrouped() ? this.createGroupedExperiment : this.createExperiment
          createExperimentMethod({data: this.getExperimentDataToSend()}).then(response => {
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

      isExperimentIdUnique () {
        return _.find(this.$store.state.experiments.experiments, e => e.id === this.experimentIdSlug) === undefined
      },

      isExperimentGroupIdUnique () {
        return _.find(this.$store.state.experimentGroups.experimentGroups, e => e === this.experimentGroup.experimentGroupName) === undefined
      },

      shouldBeGrouped () {
        return this.experimentGroup.groupWithExperiment != null && this.experimentGroup.experimentGroupName != null
      },

      getExperimentDataToSend () {
        let experimentCreationRequest = {
          id: this.experimentIdSlug,
          description: this.descriptions.description,
          documentLink: this.descriptions.documentLink,
          groups: this.descriptions.groups,
          reportingEnabled: this.reportingEnabled,
          variantNames: this.slugifiedVariants,
          internalVariantName: this.variants.internalVariantName !== '' ? this.variants.slugifiedInternalVariantName : null,
          deviceClass: this.variants.deviceClass !== 'all' ? this.variants.deviceClass : null,
          percentage: this.variants.percentage,
          reportingType: this.reportingType,
          eventDefinitions: this.eventDefinitions
        }

        if (this.shouldBeGrouped()) {
          return {
            experimentCreationRequest: experimentCreationRequest,
            experimentGroupCreationRequest: {
              id: this.experimentGroup.experimentGroupName,
              experiments: [this.experimentIdSlug, this.experimentGroup.groupWithExperiment]
            }
          }
        } else {
          return experimentCreationRequest
        }
      },
      ...mapActions([
        'createExperiment',
        'getExperiments',
        'createGroupedExperiment',
        'getExperimentGroups'
      ])
    }
  }
</script>
