<template>
  <v-list-tile @click="goToExperiment(experiment.id)">
    <v-list-tile-content>
      <v-list-tile-title>
        <span>{{ experiment.id }}</span>
        <experiment-hotness v-if="linkToData" label="Last day visits" :value="experiment.measurements.lastDayVisits"></experiment-hotness>
      </v-list-tile-title>
      <v-list-tile-sub-title v-html="experiment.desc"></v-list-tile-sub-title>

      <v-list-tile-sub-title v-if="experiment.whenStartedOrEnded()">
        {{ experiment.whenStartedOrEnded() }}
      </v-list-tile-sub-title>
    </v-list-tile-content>
    <v-list-tile-action>
      <div style="display: flex">
        <bayesian-horizontal-equalizer-chart v-if="this.showEqualizer(experiment)"
                                             :equalizerData="experiment.bayesianEqualizer"
                                             :height="20" :width="200"
        ></bayesian-horizontal-equalizer-chart>
        &nbsp;&nbsp;
        <experiment-status :experiment="experiment" :show-reporting-status="false"/>
      </div>
    </v-list-tile-action>
  </v-list-tile>
</template>

<script>
  import ExperimentHotness from './ExperimentHotness.vue'
  import ExperimentStatus from './experiment/ExperimentStatus.vue'
  import BayesianHorizontalEqualizerChart from './experiment/result/BayesianHorizontalEqualizerChart'

  export default {
    props: ['experiment', 'linkToData'],

    components: {
      ExperimentHotness,
      ExperimentStatus,
      BayesianHorizontalEqualizerChart
    },

    methods: {
      goToExperiment (experimentId) {
        return this.$router.push(`/experiments/${experimentId}`)
      },

      showEqualizer(experiment) {
        return experiment.bayesianEqualizer !== null
      }
    }
  }
</script>
