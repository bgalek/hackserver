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
      <v-badge>
        <v-chip small :color="variant.color" v-for="(variant, i) in experiment.variants" :key="variant.name"
                :disabled="true">
          {{ variant.name }}
        </v-chip>
      </v-badge>
    </v-list-tile-action>
  </v-list-tile>
</template>

<script>
  import ExperimentHotness from './ExperimentHotness.vue'

  export default {
    props: ['experiment', 'linkToData'],

    components: { ExperimentHotness },

    methods: {
      goToExperiment (experimentId) {
        console.log('goToExperiment')
        return this.$router.push(`/experiments/${experimentId}`)
      }
    }
  }
</script>
