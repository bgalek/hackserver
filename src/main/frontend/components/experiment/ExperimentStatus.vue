<template>
  <span>
    <v-chip outline disabled :color="statusColor()">
      {{ formatStatusName() }}
    </v-chip>

    <v-chip v-if="showReportingStatus" disabled outline :color="reportingEnabledButtonClass()" >
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
          ENDED: 'black',
          FULL_ON: 'black'
        }
        return colors[this.experiment.status] || 'gray'
      },

      reportingEnabledButtonClass () {
        return this.experiment.reportingEnabled ? 'green' : 'red'
      },

      reportingEnabledButtonText () {
        return this.experiment.reportingEnabled ? 'Reporting enabled' : 'Reporting disabled'
      },

      formatStatusName () {
        return this.experiment.status.replace('_', '-')
      }
    }
  }
</script>
