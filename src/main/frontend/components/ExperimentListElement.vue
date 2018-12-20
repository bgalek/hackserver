<template>

  <v-list-tile @click="goToExperiment(experiment.id)" v-if=""
               :style="[this.isEven(index) ? {} : {'background-color': 'ghostwhite'}]">

    <v-list-tile-content>
      <v-list-tile-title style="height: auto">
        <span>{{ experiment.id }}</span>

        <span v-if="experiment.experimentGroup">
          ∈
          <v-chip outline color="black" small disabled>
            {{ experiment.experimentGroup.id }}
          </v-chip>
        </span>

        <v-list-tile-sub-title v-if="experiment.getLastStatusChangeMoment()">
          {{experiment.getStatusDesc()}} {{ experiment.getLastStatusChangeMoment()}}
        </v-list-tile-sub-title>

        <div style="display: flex">
          <bayesian-horizontal-equalizer-chart v-if="this.showEqualizer(experiment)"
                                               :equalizerData="experiment.bayesianEqualizer"
                                               :height="20" :width="200"
          ></bayesian-horizontal-equalizer-chart>
        </div>

      </v-list-tile-title>
      <v-list-tile-sub-title v-html="experiment.desc"></v-list-tile-sub-title>
    </v-list-tile-content>

    <v-list-tile-action>
      <div style="display: flex">
        &nbsp;&nbsp;&nbsp;&nbsp;

        <span v-if="experiment.tags">
          <v-chip v-for="t in experiment.tags" :key="t" outline color="black" disabled>
            {{ t }}
          </v-chip>
        </span>
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
    props: ['experiment', 'linkToData', 'index'],

    components: {
      ExperimentHotness,
      ExperimentStatus,
      BayesianHorizontalEqualizerChart
    },

    methods: {
      goToExperiment (experimentId) {
        return this.$router.push(`/experiments/${experimentId}`)
      },

      showEqualizer (experiment) {
        return experiment.bayesianEqualizer !== null
      },
      isEven (index) {
        return index % 2 === 0
      }
    }
  }
</script>
