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
        <v-chip small :color="variantColor(i, variant)" v-for="(variant, i) in experiment.variants" :key="variant.name"
                :disabled="true">
          {{ variant.name }}
        </v-chip>
      </v-badge>
    </v-list-tile-action>
  </v-list-tile>
</template>

<script>
  import { variantColor } from '../utils/variantColor'
  import ExperimentHotness from './ExperimentHotness.vue'

  export default {
    props: ['experiment', 'linkToData'],

    components: { ExperimentHotness },

    methods: {
      variantColor (i, variant) {
        if (this.isBase(variant)) {
          return variantColor(0)
        } else {
          return variantColor(i + 1)
        }
      },

      goToExperiment (experimentId) {
        return this.$router.push(`/experiments/${experimentId}`)
      },

      isBase (variant) {
        return variant.name === 'base'
      }
    }
  }
</script>
