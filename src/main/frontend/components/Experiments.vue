<template>
  <v-container><v-layout><v-flex md12 lg10 xl10 offset-xl1 offset-md1 offset-lg1>

    <v-layout row>
      <v-flex xs3>
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

      <v-flex xs3>
        <v-btn-toggle v-model="statusFilter" mandatory>
          <v-btn flat value="all">
            All
          </v-btn>
          <v-btn flat value="active">
            Active
          </v-btn>
          <v-btn flat value="ended">
            Ended
          </v-btn>
          <v-btn flat value="other">
            Other
          </v-btn>
        </v-btn-toggle>
      </v-flex>
    </v-layout>

    <v-alert v-if="pivotError" color="error" icon="warning" value="true">
      Error while trying to get Pivot url: {{ pivotError }}
    </v-alert>

    <experiment-list :linkToData="true" :experiments="experiments" v-if="experiments.length"></experiment-list>
    <div v-else>There are no experiments available</div>

    <h1>Unmeasurable Experiments</h1>

    <experiment-list :linkToData="false" :experiments="immeasurableExperiments" v-if="immeasurableExperiments.length"></experiment-list>
    <div v-else>There are no experiments available</div>

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
    this.statusFilter = 'all'
    this.getExperiments()
  },

  data () {
    return {
      pivotError: false,
      filterMyExperiments: false,
      statusFilter: 'all'
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
      return sortedExperiments.filter(this.experimentFilter)
    },

    immeasurableExperiments () {
      const sortedExperiments = this.sortExperiments(_.filter(this.$store.state.experiments.experiments, (e) => !e.isMeasured))
      return sortedExperiments.filter(this.experimentFilter)
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
        if (l.isBase()) {
          return 1
        }
        if (r.isBase()) {
          return -1
        }
        return l.name.localeCompare(r.name)
      })
    },

    experimentFilter (e) {
      const myExperimentFilter = this.filterMyExperiments ? e.editable : true
      const statusFilter = this.statusFilter === 'all' ||
        (this.statusFilter === 'other' && ['PLANNED', 'DRAFT', 'PAUSED'].includes(e.status)) ||
        (this.statusFilter.toUpperCase() === e.status)
      return myExperimentFilter && statusFilter
    }
  }
}
</script>

