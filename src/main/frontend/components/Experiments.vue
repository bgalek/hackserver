<template>
  <v-container><v-layout><v-flex offset-md1 md10 lg10 offset-xl1 xl10>
    <h1>Experiments</h1>

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

  mounted () {
    this.getExperiments()
  },

  data () {
    return {
      pivotError: false
    }
  },

  computed: {
    ...mapState({
      error: state => state.experiments.error.experiments,
      pending: state => state.experiments.pending.experiments
    }),

    experiments () {
      return this.sortExperiments(_.filter(this.$store.state.experiments.experiments, (e) => e.isMeasured))
    },

    immeasurableExperiments () {
      return this.sortExperiments(_.filter(this.$store.state.experiments.experiments, (e) => !e.isMeasured))
    }
  },

  methods: {
    ...mapActions(['getExperiments']),

    sortExperiments (experiments) {
      experiments = _.cloneDeep(experiments)
      experiments.forEach(e => this.sortVariants(e.variants))

      const sortingKey = function (experiment) {
        return experiment.measurements.lastDayVisits.toString().padStart(8, '0') +
          (experiment.activeFrom ? experiment.fromDateString() : '0') +
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

