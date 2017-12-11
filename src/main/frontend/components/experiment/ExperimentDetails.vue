<template>
  <chi-panel title="Details">
        <h3>Description</h3>
        {{ experiment.description }}
        <h3>Owner</h3>
        {{ experiment.owner }}
        <div v-if="experiment.activeFrom || experiment.activeTo">
          <h3>Dates</h3>
          <v-layout row>
            <v-flex xs2>Active from: </v-flex><v-flex xs9>{{ experiment.fromDateString() }}</v-flex>
          </v-layout>
          <v-layout row>
            <v-flex xs2>Active to: </v-flex><v-flex xs9ą>{{ experiment.toDateString() }}</v-flex>
          </v-layout>
        </div>
        <h3>Status</h3>
        <v-layout row>

            <v-chip outline :color="activityButtonClass()">
              {{ experiment.status() }}
            </v-chip>

            <v-chip outline :color="reportingEnabledButtonClass()">
              {{ reportingEnabledButtonText() }}
            </v-chip>

        </v-layout>
  </chi-panel>
</template>

<script>
  import ChiPanel from '../ChiPanel.vue'

  export default {
    props: ['experiment'],

    components: {
      ChiPanel
    },

    methods: {
      activityButtonClass () {
        let colors = {
          PLANNED: 'blue',
          ACTIVE: 'green',
          ENDED: 'black'
        }
        return colors[this.experiment.status()]
      },

      reportingEnabledButtonClass () {
        return this.experiment.reportingEnabled ? 'green' : 'red'
      },

      reportingEnabledButtonText () {
        return this.experiment.reportingEnabled ? 'Reporting enabled' : 'Reporting disabled'
      }
    }
  }
</script>
