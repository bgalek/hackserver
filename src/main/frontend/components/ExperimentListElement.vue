<template>
  <v-list-tile @click="goToExperiment(experiment.id)">
    <v-list-tile-content>
      <v-list-tile-title>
        <span>{{ experiment.id }}</span>
        <experiment-hotness v-if="linkToData" label="Last day visits" :value="experiment.measurements.lastDayVisits"></experiment-hotness>
      </v-list-tile-title>
      <v-list-tile-sub-title v-html="experiment.desc"></v-list-tile-sub-title>

      <v-list-tile-sub-title v-if="experiment.fromDateShortString()">
        Started {{ experiment.fromDateShortString() }}
      </v-list-tile-sub-title>

    </v-list-tile-content>
    <v-list-tile-action>
      <experiment-status :experiment="experiment" :show-reporting-status="false"/>
    </v-list-tile-action>
  </v-list-tile>
</template>

<script>
  import ExperimentHotness from './ExperimentHotness.vue'
  import ExperimentStatus from './experiment/ExperimentStatus.vue'

  export default {
    props: ['experiment', 'linkToData'],

    components: {
      ExperimentHotness,
      ExperimentStatus
    },

    methods: {
      goToExperiment (experimentId) {
        return this.$router.push(`/experiments/${experimentId}`)
      }
    }
  }
</script>
