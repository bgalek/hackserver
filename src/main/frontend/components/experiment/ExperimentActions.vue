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

    <v-alert v-if="commandOkMessage" outline icon="info" type="success" v-model="commandOkMessage"
             dismissible>
      {{commandOkMessage}}
    </v-alert>

    <v-form ref="actionForm"
            v-model="actionFormValid"
            v-if="this.canRunLifecycleCommand()"
            lazy-validation>

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
        Start experiment<v-icon right dark>play_arrow</v-icon>
      </v-btn>

      <v-menu :close-on-content-click="false"
              v-model="prolongMenuVisible"
              v-if="this.experiment.canBeProlonged()">
        <v-btn color="gray" slot="activator" style="text-transform: none">
          Prolong <v-icon right>alarm_add</v-icon>
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
          Pause<v-icon right>pause</v-icon>
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
          Resume<v-icon right>play_arrow</v-icon>
        </v-btn>
        <v-list>
          <v-list-tile @click="resume">
            <v-list-tile-title>I really want to resume experiment <b>{{this.experiment.id}}</b></v-list-tile-title>
          </v-list-tile>
        </v-list>
      </v-menu>


      <v-menu bottom offset-y
              v-if="this.experiment.canBeStopped()">
        <v-btn color="gray" slot="activator" style="text-transform: none">
          Stop <v-icon right>stop</v-icon>
        </v-btn>
        <v-list>
          <v-list-tile @click="stop">
            <v-list-tile-title>I really want to stop experiment <b>{{this.experiment.id}}</b></v-list-tile-title>
          </v-list-tile>
        </v-list>
      </v-menu>

    <div v-if="canRunOtherCommand">
      <h4 style="margin-top: 15px">Other actions</h4>

      <v-menu :close-on-content-click="false"
              v-model="descriptionsMenuVisible">
        <v-btn color="gray" slot="activator" style="text-transform: none">
          Update descriptions
          <v-icon right>format_align_left</v-icon>
        </v-btn>

        <v-list style="padding:15px; display: block;">
          <experiment-desc-editing :experiment="experiment"
                                   v-model="descriptionsEditingResult"
          />

          <v-btn flat @click="closeDescriptions()">Cancel</v-btn>
          <v-btn color="gray"
                 :disabled="!descriptionsChanged()"
                 @click="updateDescriptions"
                 style="text-transform: none">
            Update descriptions of &nbsp;<b>{{ this.experiment.id }}</b>
          </v-btn>
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
  import ChiPanel from '../ChiPanel.vue'
  import { mapActions } from 'vuex'

  export default {
    props: ['experiment', 'allowDelete'],

    components: {
      ChiPanel,
      ExperimentDescEditing
    },

    data () {
      return {
        descriptionsMenuVisible: false,
        descriptionsEditingResult: {},
        prolongMenuVisible: false,
        commandOkMessage: '',
        actionFormValid: true,
        durationDays: '14',
        additionalDurationDays: '14',
        durationDaysRules: [
          (v) => !!v || 'duration is required',
          (v) => parseInt(v).toString() === v || 'seriously?',
          (v) => v <= 60 || 'more than 60 days? Seems like a long time...',
          (v) => v > 0 || 'try with a positive value'
        ],
        sendingDataToServer: false,
        errors: []
      }
    },

    methods: {
      canRunLifecycleCommand () {
        return this.experiment.definition.canRunLifecycleCommand()
      },

      canRunAnyCommand () {
        return this.canRunLifecycleCommand()
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
        if (this.$refs.actionForm.validate()) {
          this.prepareToSend()

          this.startExperiment({
            data: {
              experimentDurationDays: this.durationDays
            },
            params: {
              experimentId: this.experiment.id
            }
          }).then(response => {
            this.getExperiment({params: {experimentId: this.experiment.id}})
            this.commandOkMessage = 'Experiment successfully started'
          }).catch(error => {
            this.showError(error)
          })

          this.afterSending()
        }
      },

      stop () {
        this.prepareToSend()
        this.stopExperiment({
          params: {experimentId: this.experiment.id}
        }).then(response => {
          this.afterSending()
          this.getExperiment({params: {experimentId: this.experiment.id}})
          this.commandOkMessage = 'Experiment successfully stopped'
        }).catch(error => {
          this.afterSending()
          this.showError(error)
        })
      },

      pause () {
        this.prepareToSend()
        this.pauseExperiment({
          params: {experimentId: this.experiment.id}
        }).then(response => {
          this.afterSending()
          this.getExperiment({params: {experimentId: this.experiment.id}})
          this.commandOkMessage = 'Experiment successfully paused'
        }).catch(error => {
          this.afterSending()
          this.showError(error)
        })
      },

      resume () {
        this.prepareToSend()
        this.resumeExperiment({
          params: {experimentId: this.experiment.id}
        }).then(response => {
          this.afterSending()
          this.getExperiment({params: {experimentId: this.experiment.id}})
          this.commandOkMessage = 'Experiment successfully resumed'
        }).catch(error => {
          this.afterSending()
          this.showError(error)
        })
      },

      closeProlong () {
        this.prolongMenuVisible = false
      },

      closeDescriptions () {
        this.descriptionsMenuVisible = false
      },

      descriptionsChanged () {
        return this.descriptionsEditingResult.description !== this.experiment.description ||
               JSON.stringify(this.descriptionsEditingResult.groups) !== JSON.stringify(this.experiment.groups) ||
               this.descriptionsEditingResult.documentLink !== this.experiment.documentLink
      },

      updateDescriptions () {
        if (this.$refs.actionForm.validate()) {
          this.closeDescriptions()
          this.prepareToSend()
          this.updateExperimentDescriptions({
            data: {
              description: this.descriptionsEditingResult.description,
              groups: this.descriptionsEditingResult.groups,
              documentLink: this.descriptionsEditingResult.documentLink
            },
            params: {
              experimentId: this.experiment.id
            }
          }).then(response => {
            this.afterSending()
            this.getExperiment({params: {experimentId: this.experiment.id}})
            this.commandOkMessage = 'Experiment  successfully updated'
          }).catch(error => {
            this.afterSending()
            this.showError(error)
          })

          this.descriptionsMenuVisible = false
        }
      },

      prolong () {
        if (this.$refs.actionForm.validate()) {
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
            this.getExperiment({params: {experimentId: this.experiment.id}})
            this.commandOkMessage = 'Experiment successfully prolonged'
          }).catch(error => {
            this.showError(error)
          })

          this.afterSending()
        }
      },

      showError (error) {
        this.errors.push(JSON.stringify(error))
      },

      prepareToSend () {
        this.commandOkMessage = ''
        this.errors = []
        this.sendingDataToServer = true
      },

      afterSending () {
        this.sendingDataToServer = false
      },

      showProgressBar () {
        return this.sendingDataToServer
      },

      ...mapActions([
        'startExperiment',
        'getExperiment',
        'deleteExperiment',
        'stopExperiment',
        'pauseExperiment',
        'resumeExperiment',
        'prolongExperiment',
        'updateExperimentDescriptions'
      ])
    }
  }
</script>
