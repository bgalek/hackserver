<template>
  <chi-panel title="Owner zone" v-if="experiment.editable">
    <v-alert v-for="error in errors"
             color="error" icon="warning" value="true" :key="error">
      {{ error }}
    </v-alert>

    <v-progress-linear v-if="showProgressBar()" v-bind:indeterminate="true">
    </v-progress-linear>

    <span v-if="!this.experiment.canRunAnyCommand() && !commandOkMessage">
      Not much to do here.
    </span>

    <v-alert v-if="commandOkMessage" outline icon="info" type="success" v-model="commandOkMessage"
             dismissible>
      {{commandOkMessage}}
    </v-alert>

    <v-form ref="actionForm"
            v-model="actionFormValid"
            v-if="this.experiment.canRunAnyCommand()"
            lazy-validation>

      <v-text-field
             v-if="this.experiment.canBeStarted()"
             label="Duration days"
             v-model="durationDays"
             :rules="durationDaysRules"
             required
      ></v-text-field>

      <v-btn v-if="this.experiment.canBeStarted()"
             color="green"
             @click="start"
             style="text-transform: none">
        Start experiment
      </v-btn>

      <v-text-field
             v-if="this.experiment.canBeProlonged()"
             label="Additional duration days"
             v-model="additionalDurationDays"
             :rules="durationDaysRules"
             required
      ></v-text-field>

      <v-btn v-if="this.experiment.canBeProlonged()"
             color="gray"
             @click="prolong"
             style="text-transform: none">
        Prolong experiment
      </v-btn>

      <v-menu open-on-hover bottom offset-y
              v-if="this.experiment.canBeStopped()">
        <v-btn color="gray" slot="activator" style="text-transform: none">Stop experiment
        </v-btn>
        <v-list>
          <v-list-tile @click="stop">
            <v-list-tile-title>I really want to stop experiment {{this.experiment.id}}</v-list-tile-title>
            &nbsp;<v-icon right>alarm</v-icon>
          </v-list-tile>
        </v-list>
      </v-menu>

      <v-menu open-on-hover bottom offset-y
              v-if="this.experiment.canBePaused()">
        <v-btn color="gray" slot="activator" style="text-transform: none">Pause experiment
        </v-btn>
        <v-list>
          <v-list-tile @click="pause">
            <v-list-tile-title>I really want to pause experiment {{this.experiment.id}}</v-list-tile-title>
            &nbsp;<v-icon right>alarm</v-icon>
          </v-list-tile>
        </v-list>
      </v-menu>

      <v-menu open-on-hover bottom offset-y
              v-if="this.experiment.canBeResumed()">
        <v-btn color="gray" slot="activator" style="text-transform: none">Resume experiment
        </v-btn>
        <v-list>
          <v-list-tile @click="resume">
            <v-list-tile-title>I really want to resume paused experiment {{this.experiment.id}}</v-list-tile-title>
            &nbsp;<v-icon right>alarm</v-icon>
          </v-list-tile>
        </v-list>
      </v-menu>

      <v-menu open-on-hover bottom offset-y
              v-if="canBeDeleted()">
        <v-btn color="red" slot="activator" style="text-transform: none">Delete experiment
        </v-btn>
        <v-list>
          <v-list-tile @click="deleteMe">
            <v-list-tile-title>I really want to delete experiment {{this.experiment.id}}</v-list-tile-title>
            &nbsp;<v-icon right>delete_forever</v-icon>
          </v-list-tile>
        </v-list>
      </v-menu>

    </v-form>

  </chi-panel>
</template>

<script>
  import ChiPanel from '../ChiPanel.vue'
  import { mapActions } from 'vuex'

  export default {
    props: ['experiment', 'allowDelete'],

    components: {
      ChiPanel
    },

    data () {
      return {
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

      canBeDeleted () {
        return this.allowDelete
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

      prolong () {
        if (this.$refs.actionForm.validate()) {
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

      ...mapActions(['startExperiment', 'getExperiment', 'deleteExperiment', 'stopExperiment', 'pauseExperiment', 'resumeExperiment', 'prolongExperiment'])
    }
  }
</script>
