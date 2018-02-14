<template>
  <v-container><v-layout><v-flex md12 lg10 offset-xl1 xl10>

    <v-layout row justify-space-between>
      <v-flex xs2>
        <h1>Experiments</h1>
      </v-flex>
      <v-flex xs3>
        <v-switch
          v-on:change="updateMyExperimentsFilter"
          label="My Experiments"
          v-model="filterMyExperiments"
        >
        </v-switch>
      </v-flex>
    </v-layout>

    <v-alert v-if="pivotError" color="error" icon="warning" value="true">
      Error while trying to get Pivot url: {{ pivotError }}
    </v-alert>

    <experiment-list :linkToData="true" :experiments="experiments" v-if="experiments.length"></experiment-list>

    <h1>Unmeasurable Experiments</h1>

    <experiment-list :linkToData="false" :experiments="immeasurableExperiments" v-if="immeasurableExperiments.length"></experiment-list>


    <v-alert v-if="error" color="error" icon="warning" value="true">
      Couldn't load experiments: {{ error.message }}
    </v-alert>

    <p class="text-xs-center">
      <v-progress-circular v-if="pending" indeterminate :size="70" :width="7" color="purple"></v-progress-circular>
    </p>
  </v-flex></v-layout></v-container>
</template>

<script>
import { mapState, mapActions } from 'vuex'
import ExperimentList from './ExperimentList.vue'
import _ from 'lodash'

export default {
  components: {
    ExperimentList
  },

  created () {
    this.filterMyExperiments = this.userPreferences.filters.myExperiments
    this.getExperiments()
  },

  data () {
    return {
      pivotError: false,
      filterMyExperiments: false
    }
  },

  computed: {
    ...mapState({
      error: state => state.experiments.error.experiments,
      pending: state => state.experiments.pending.experiments,
      userPreferences: state => state.userPreferences
    }),

    experiments () {
      const sortedExperiments = this.sortExperiments(_.filter(this.$store.state.experiments.experiments, (e) => e.isMeasured))
      return sortedExperiments.filter((e) => {
        return this.filterMyExperiments ? e.editable : true
      })
    },

    immeasurableExperiments () {
      const sortedExperiments = this.sortExperiments(_.filter(this.$store.state.experiments.experiments, (e) => !e.isMeasured))
      return sortedExperiments.filter((e) => {
        return this.filterMyExperiments ? e.editable : true
      })
    }
  },

  methods: {
    ...mapActions([
      'getExperiments',
      'updateMyExperimentsFilter'
    ]),

    sortExperiments (experiments) {
      experiments = _.cloneDeep(experiments)
      experiments.forEach(e => this.sortVariants(e.variants))

      const sortingKey = function (experiment) {
        return experiment.measurements.lastDayVisits.toString().padStart(8, '0') +
          (experiment.activityPeriod ? experiment.fromDateString() : '0') +
          experiment.id
      }

      experiments.sort((l, r) => sortingKey(r).localeCompare(sortingKey(l)))

      return experiments
    },

    sortVariants (variants) {
      variants.sort((l, r) => {
        if (this.isBase(l)) {
          return 1
        }
        if (this.isBase(r)) {
          return -1
        }
        return l.name.localeCompare(r.name)
      })
    },

    isBase (variant) {
      return variant.name === 'base'
    }
  }
}
</script>

