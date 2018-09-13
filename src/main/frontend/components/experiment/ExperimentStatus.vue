<template>
  <span>
    <v-chip outline disabled :color="statusColor()">
      {{ formatStatusName() }}
    </v-chip>
  </span>
</template>

<script>
  export default {
    props: ['experiment'],

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

      formatStatusName () {
        return this.experiment.status.replace('_', '-')
      }
    }
  }
</script>
