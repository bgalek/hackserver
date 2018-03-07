<template>
  <span>
    <v-chip outline disabled :color="statusColor()">
      {{ experiment.status }}
    </v-chip>

    <v-chip v-if="showReportingStatus===true" disabled outline :color="reportingEnabledButtonClass()" >
      {{ reportingEnabledButtonText() }}
    </v-chip>
  </span>
</template>

<script>
  export default {
    props: ['experiment', 'showReportingStatus'],

    methods: {
      statusColor () {
        let colors = {
          DRAFT: 'gray',
          PLANNED: 'blue',
          ACTIVE: 'green',
          ENDED: 'black'
        }
        return colors[this.experiment.status] || 'gray'
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
