<template>
  <chi-panel title="Owner zone" v-if="experiment.editable">
    <v-alert v-for="error in errors"
             color="error" icon="warning" value="true" :key="error">
      {{ error }}
    </v-alert>

    <v-progress-linear v-if="showProgressBar()" v-bind:indeterminate="true">
    </v-progress-linear>

    <span v-if="!this.canRunAnyCommand() && !commandOkMessage">
      Not much to do here.
    </span>

    <v-alert v-if="commandOkMessage" outline type="success" v-model="commandOkMessage"
             dismissible>
      {{commandOkMessage}}
    </v-alert>

    <v-alert v-if="descriptionsWarning" outline icon="sentiment_very_dissatisfied" type="warning" v-model="descriptionsWarning"
             dismissible>
      {{descriptionsWarning}}
    </v-alert>


    <v-form ref="actionForm"
            v-model="actionFormValid"

            lazy-validation>
      <div v-if="this.canRunLifecycleCommand()">
        <h4 style="margin-top: 5px">Lifecycle actions</h4>

        <v-text-field
          style="width: 200px"
          v-if="this.experiment.canBeStarted()"
          label="Duration days"
          v-model="durationDays"
          :rules="durationDaysRules"
          required
        ></v-text-field>

        <v-btn v-if="this.experiment.canBeStarted()"
               color="green"
               @click="start"
               style="text-transform: none"
               class="white--text">
          Start experiment
          <v-icon right dark>play_arrow</v-icon>
        </v-btn>

        <v-menu :close-on-content-click="false"
                v-model="prolongMenuVisible"
                v-if="this.experiment.canBeProlonged()">
          <v-btn color="gray" slot="activator" style="text-transform: none">
            Prolong
            <v-icon right>alarm_add</v-icon>
          </v-btn>

          <v-list style="padding:15px; display: block;">
            <v-text-field
              label="Additional days"
              v-model="additionalDurationDays"
              :rules="durationDaysRules"
              required
            ></v-text-field>

            <v-btn flat @click="closeProlong()">Cancel</v-btn>
            <v-btn color="gray"
                   @click="prolong"
                   style="text-transform: none">
              Prolong experiment &nbsp;<b>{{ this.experiment.id }}</b>
            </v-btn>
          </v-list>
        </v-menu>

        <v-menu bottom offset-y
                v-if="this.experiment.canBePaused()">
          <v-btn color="gray" slot="activator" style="text-transform: none">
            Pause
            <v-icon right>pause</v-icon>
          </v-btn>
          <v-list>
            <v-list-tile @click="pause">
              <v-list-tile-title>I really want to pause experiment <b>{{this.experiment.id}}</b></v-list-tile-title>
            </v-list-tile>
          </v-list>
        </v-menu>

        <v-menu bottom offset-y
                v-if="this.experiment.canBeResumed()">
          <v-btn color="gray" slot="activator" style="text-transform: none">
            Resume
            <v-icon right>play_arrow</v-icon>
          </v-btn>
          <v-list>
            <v-list-tile @click="resume">
              <v-list-tile-title>I really want to resume experiment <b>{{this.experiment.id}}</b></v-list-tile-title>
            </v-list-tile>
          </v-list>
        </v-menu>

        <v-menu :close-on-content-click="false"
                v-model="fullOnMenuVisible"
                v-if="this.experiment.canBeFullOn()">
          <v-btn color="gray" slot="activator" style="text-transform: none">
            Full-on
            <v-icon right>check_circle</v-icon>
          </v-btn>
          <v-list style="padding:15px; display: block;">
            <v-select
              v-model="selectedFullOnVariant"
              :items="allVariantNames()"
              label="Choose variant"
            ></v-select>
            <v-btn flat @click="closeFullOn()">Cancel</v-btn>
            <v-btn color="gray"
                   :disabled="!fullOnVariantSelected()"
                   @click="fullOn"
                   style="text-transform: none">
              Make experiment &nbsp;<b>{{ this.experiment.id }}</b>&nbsp; full-on
            </v-btn>
          </v-list>
        </v-menu>

        <v-menu bottom offset-y
                v-if="this.experiment.canBeStopped()">
          <v-btn color="gray" slot="activator" style="text-transform: none">
            Stop
            <v-icon right>stop</v-icon>
          </v-btn>
          <v-list>
            <v-list-tile @click="stop">
              <v-list-tile-title>I really want to stop experiment <b>{{this.experiment.id}}</b></v-list-tile-title>
            </v-list-tile>
          </v-list>
        </v-menu>
      </div>

      <div v-if="canRunOtherCommand()">
        <h4 style="margin-top: 15px">Other actions</h4>

        <v-menu :close-on-content-click="false"
                v-model="descriptionsMenuVisible">
          <v-btn color="gray" slot="activator" style="text-transform: none">
            Update descriptions
            <v-icon right>format_align_left</v-icon>
          </v-btn>

          <v-list style="padding:15px; display: block;">
            <experiment-desc-editing :experiment="experiment"
                                     :available-experiment-tags="availableExperimentTags"
                                     show-buttons="true"
                                     @updateDescriptions="updateDescriptions"
                                     @closeDescriptions="closeDescriptions"
            ></experiment-desc-editing>
          </v-list>
        </v-menu>

        <v-menu :close-on-content-click="false"
                v-if="this.experiment.canChangeVariants()"
                v-model="variantsMenuVisible">
          <v-btn color="gray" slot="activator" style="text-transform: none">
            Update variants
            <v-icon right>settings</v-icon>
          </v-btn>

          <v-list style="padding:15px; display: block;">
            <experiment-variants-editing :experiment="experiment"
                                         show-buttons="true"
                                         @updateVariants="updateVariants"
                                         @closeVariants="closeVariants"
                                         :allowModifyRegularVariants="false"
            ></experiment-variants-editing>
          </v-list>
        </v-menu>

        <v-menu :close-on-content-click="false"
                :close-on-click="false"
                v-if="this.experiment.canChangeEventDefinitions()"
                v-model="eventDefinitionsMenuVisible">
          <v-btn color="gray" slot="activator" style="text-transform: none">
            Update NGA event definitions
            <v-icon right>visibility</v-icon>
          </v-btn>

          <v-list style="padding:15px; display: block;">
            <experiment-event-filters-editing
              :experiment="experiment"
              :showButtons="true"
              @updateEventDefinitions="updateEventDefinitions"
              @closeEventDefinitions="closeEventDefinitions">
            </experiment-event-filters-editing>
          </v-list>
        </v-menu>

        <v-menu :close-on-content-click="false"
                v-model="addToGroupMenuVisible"
                v-if="this.experiment.canBeGrouped()">

          <v-btn color="gray" slot="activator" style="text-transform: none"
                 @click="loadExprimentGroups">
            Group
            <v-icon right>view_quilt</v-icon>
          </v-btn>

          <v-list style="padding:15px; display: block;">
            <experiment-add-to-group-editing style="margin-bottom: 15px"
              v-if="loadingExperimentGroupsDone"
              :experiment="experiment"
              :experimentGroupNames="experimentGroups"
              @addToGroup="addToGroup"
              @closeAddToGroup="closeAddToGroup"
            ></experiment-add-to-group-editing>
          </v-list>
        </v-menu>

        <v-menu bottom offset-y
                v-if="this.allowDelete">
          <v-btn color="red" slot="activator" class="white--text" style="text-transform: none">
            Delete experiment
            <v-icon right>delete_forever</v-icon>
          </v-btn>
          <v-list>
            <v-list-tile @click="deleteMe">
              <v-list-tile-title>I really want to delete experiment <b>{{this.experiment.id}}</b></v-list-tile-title>
              &nbsp;
            </v-list-tile>
          </v-list>
        </v-menu>
      </div>
    </v-form>
  </chi-panel>
</template>

<script>
  import ExperimentDescEditing from './ExperimentDescEditing.vue'
  import ExperimentAddToGroupEditing from './ExperimentAddToGroupEditing.vue'
  import ExperimentVariantsEditing from './ExperimentVariantsEditing.vue'
  import ExperimentEventFiltersEditing from './ExperimentEventFiltersEditing'
  import ChiPanel from '../ChiPanel.vue'
  import {mapState, mapActions} from 'vuex'
  import { formatError } from '../../model/errors'

  export default {
    props: ['experiment', 'allowDelete', 'availableExperimentTags'],

    components: {
      ExperimentAddToGroupEditing,
      ChiPanel,
      ExperimentDescEditing,
      ExperimentVariantsEditing,
      ExperimentEventFiltersEditing
    },

    data () {
      return {
        descriptionsMenuVisible: false,
        variantsMenuVisible: false,
        eventDefinitionsMenuVisible: false,
        prolongMenuVisible: false,
        addToGroupMenuVisible: false,
        fullOnMenuVisible: false,
        commandOkMessage: '',
        descriptionsWarning: '',
        warningMessage: '',
        actionFormValid: true,
        durationDays: '14',
        selectedFullOnVariant: null,
        additionalDurationDays: '14',
        durationDaysRules: [
          (v) => !!v || 'duration is required',
          (v) => parseInt(v).toString() === v || 'seriously?',
          (v) => v <= 60 || 'more than 60 days? Seems like a long time...',
          (v) => v > 0 || 'try with a positive value'
        ],
        loadingExperimentGroupsDone: false,
        sendingDataToServer: false,
        errors: []
      }
    },

    computed: mapState({
      experimentGroups (state) {
        return state.experimentGroups.experimentGroups || { }
      }
    }),

    methods: {
      canRunLifecycleCommand () {
        return this.experiment.canRunLifecycleCommand()
      },

      canRunOtherCommand () {
        return true
      },

      canRunAnyCommand () {
        return this.canRunLifecycleCommand() || this.canRunOtherCommand()
      },

      deleteMe () {
        this.prepareToSend()
        this.deleteExperiment({
          params: {
            experimentId: this.experiment.id
          }
        }).then(response => {
          console.log('delete.response', response.status)
          return this.$router.push(`/experiments`)
        }).catch(error => {
          this.showError(error)
        })
        this.afterSending()
      },

      start () {
        if (this.validate() && this.descriptionsAreFine()) {
          this.prepareToSend()

          this.startExperiment({
            data: {
              experimentDurationDays: this.durationDays
            },
            params: {
              experimentId: this.experiment.id
            }
          }).then(response => {
            this.getExperiment(this.experiment.id)
            this.commandOkMessage = 'Experiment successfully started'
          }).catch(error => {
            this.showError(error)
          })

          this.afterSending()
        }
      },

      fullOn () {
        this.prepareToSend()
        this.makeExperimentFullOn({
          params: {experimentId: this.experiment.id},
          data: {variantName: this.selectedFullOnVariant}
        }).then(response => {
          this.getExperiment(this.experiment.id)
          this.commandOkMessage = 'Experiment made full-on'
        }).catch(error => {
          this.showError(error)
        })

        this.afterSending()
      },

      stop () {
        this.prepareToSend()
        this.stopExperiment({
          params: {experimentId: this.experiment.id}
        }).then(response => {
          this.getExperiment(this.experiment.id)
          this.commandOkMessage = 'Experiment successfully stopped'
        }).catch(error => {
          this.showError(error)
        })

        this.afterSending()
      },

      pause () {
        this.prepareToSend()
        this.pauseExperiment({
          params: {experimentId: this.experiment.id}
        }).then(response => {
          this.getExperiment(this.experiment.id)
          this.commandOkMessage = 'Experiment successfully paused'
        }).catch(error => {
          this.showError(error)
        })

        this.afterSending()
      },

      resume () {
        this.prepareToSend()
        this.resumeExperiment({
          params: {experimentId: this.experiment.id}
        }).then(response => {
          this.getExperiment(this.experiment.id)
          this.commandOkMessage = 'Experiment successfully resumed'
        }).catch(error => {
          this.showError(error)
        })

        this.afterSending()
      },

      closeProlong () {
        this.prolongMenuVisible = false
      },

      allVariantNames () {
        const variantNames = this.experiment.variantNames
        const internalVariantName = this.experiment.internalVariantName
        return internalVariantName != null && !variantNames.contains(internalVariantName)
          ? variantNames.toArray().concat([internalVariantName])
          : variantNames.toArray()
      },

      fullOnVariantSelected () {
        return this.selectedFullOnVariant != null
      },

      closeFullOn () {
        this.selectedFullOnVariant = null
        this.fullOnMenuVisible = false
      },

      closeAddToGroup () {
        this.addToGroupMenuVisible = false
      },

      descriptionsAreFine () {
        this.clearMessages()

        if (!this.experiment.description || this.experiment.description.length < 10) {
          this.descriptionsWarning = "Experiment's description '" + this.experiment.description + "' doesn't seem very descriptive." +
            ' Please update it. What feature are you going to test and where it is?'
          return false
        }

        if (!this.experiment.documentLink) {
          this.descriptionsWarning = 'Documentation link is missing. Please add it. For example https://wiki.allegrogroup.com/pages/viewpage.action?pageId=328926207'
          return false
        }

        return true
      },

      updateDescriptions (experimentDescEditingRecord) {
        this.prepareToSend()
        this.updateExperimentDescriptions({
          data: experimentDescEditingRecord,
          params: {
            experimentId: this.experiment.id
          }
        }).then(response => {
          this.getExperiment(this.experiment.id)
          this.commandOkMessage = 'Experiment  successfully updated'
        }).catch(error => {
          this.showError(error)
        })

        this.afterSending()
        this.closeDescriptions()
      },

      closeDescriptions () {
        this.descriptionsMenuVisible = false
      },

      closeVariants () {
        this.variantsMenuVisible = false
      },

      closeEventDefinitions () {
        this.eventDefinitionsMenuVisible = false
      },

      updateVariants (variantsEditingResult) {
        this.prepareToSend()

        this.updateExperimentVariants({
          data: variantsEditingResult,
          params: {
            experimentId: this.experiment.id
          }
        }).then(response => {
          this.getExperiment(this.experiment.id)
          this.commandOkMessage = 'Experiment successfully updated'
        }).catch(error => {
          this.showError(error)
        })

        this.afterSending()
        this.closeVariants()
      },

      updateEventDefinitions (eventDefinitionsEditingResult) {
        this.closeEventDefinitions()
        this.prepareToSend()
        this.updateExperimentEventDefinitions({
          data: eventDefinitionsEditingResult,
          params: {
            experimentId: this.experiment.id
          }
        }).then(response => {
          this.getExperiment(this.experiment.id)
          this.commandOkMessage = 'Experiment successfully updated'
        }).catch(error => {
          this.showError(error)
        })

        this.afterSending()
        this.closeEventDefinitions()
      },

      prolong () {
        if (this.validate()) {
          this.closeProlong()
          this.prepareToSend()

          this.prolongExperiment({
            data: {
              experimentAdditionalDays: this.additionalDurationDays
            },
            params: {
              experimentId: this.experiment.id
            }
          }).then(response => {
            this.getExperiment(this.experiment.id)
            this.commandOkMessage = 'Experiment successfully prolonged'
          }).catch(error => {
            this.showError(error)
          })

          this.afterSending()
        }
      },

      addToGroup (groupName) {
        this.prepareToSend()

        this.addExperimentToGroup({
          data: {
            experimentId: this.experiment.id,
            id: groupName
          }
        }).then(response => {
          this.getExperiment(this.experiment.id)
          this.commandOkMessage = 'Experiment successfully added to group ' + groupName
        }).catch(error => {
          this.showError(error)
        })

        this.afterSending()
        this.closeAddToGroup()
      },

      loadExprimentGroups () {
        this.getExperimentGroups().then(() => {
          this.loadingExperimentGroupsDone = true
        })
      },

      showError (error) {
        this.errors.push(formatError(error))
      },

      clearMessages () {
        this.commandOkMessage = ''
        this.descriptionsWarning = ''
      },

      prepareToSend () {
        this.clearMessages()
        this.errors = []
        this.sendingDataToServer = true
      },

      afterSending () {
        this.sendingDataToServer = false
      },

      showProgressBar () {
        return this.sendingDataToServer
      },

      validate () {
        return this.$refs.actionForm.validate()
      },

      ...mapActions([
        'startExperiment',
        'getExperiment',
        'deleteExperiment',
        'stopExperiment',
        'makeExperimentFullOn',
        'pauseExperiment',
        'resumeExperiment',
        'prolongExperiment',
        'updateExperimentDescriptions',
        'updateExperimentVariants',
        'updateExperimentEventDefinitions',
        'addExperimentToGroup',
        'getExperimentGroups'
      ])
    }
  }
</script>
