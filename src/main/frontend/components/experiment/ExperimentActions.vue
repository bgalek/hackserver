<template>
  <chi-panel title="Owner zone" v-if="experiment.editable">
    <v-alert v-for="error in errors"
             color="error" icon="warning" value="true" :key="error">
      {{ error }}
    </v-alert>

    <v-progress-linear v-if="showProgressBar()" v-bind:indeterminate="true">
    </v-progress-linear>

    <span v-if="!canRunAnyCommand() && !commandOkMessage">
      Not much to do here.
    </span>

    <v-alert v-if="commandOkMessage" outline icon="info" type="success" v-model="commandOkMessage"
             dismissible>
      {{commandOkMessage}}
    </v-alert>

    <v-form ref="actionForm"
            v-model="actionFormValid"
            v-if="canBeStarted()"
            lazy-validation>
      <v-text-field
        label="Duration days"
        v-model="durationDays"
        :rules="durationDaysRules"
        required
      ></v-text-field>

      <v-btn color="green" @click="start">
        Start experiment
      </v-btn>
    </v-form>

  </chi-panel>
</template>

<script>
  import ChiPanel from '../ChiPanel.vue'
  import { mapActions } from 'vuex'

  export default {
    props: ['experiment'],

    components: {
      ChiPanel
    },

    data () {
      return {
        commandOkMessage: '',
        actionFormValid: true,
        durationDays: '14',
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

      start () {
        if (this.$refs.actionForm.validate()) {
          this.prepareToSend()

          this.startExperiment({data: this.buildStartExperimentCommand()}).then(response => {
            this.getExperiment({ params: { experimentId: this.experiment.id } })
            this.commandOkMessage = 'Experiment successfully started'
          }).catch(error => {
            this.showError(error)
          })

          this.afterSending()
        }
      },

      buildStartExperimentCommand () {
        return {
          experimentId: this.experiment.id,
          experimentDurationDays: this.durationDays
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

      canBeStarted () {
        return this.experiment.status === 'DRAFT'
      },

      canRunAnyCommand () {
        return this.canBeStarted()
      },

      ...mapActions(['startExperiment', 'getExperiment'])
    }
  }
</script>
