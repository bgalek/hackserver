<template>
  <v-container>
    <v-layout>
      <v-flex offset-md1 md10 lg9 offset-xl2 xl8>

        <h1>Experiment: {{ $route.params.experimentId }}</h1>

        <experiment-details
          :experiment="experiment"
        ></experiment-details>

        <chi-panel title="Metrics & Statistics">
          <result-table
            v-if="experiment.reportingEnabled"
            :experiment="experiment"
          ></result-table>

          <div slot="footer">
            Read the χ Docs about <a href="https://rtd.allegrogroup.com/docs/chi/pl/latest/chi_metrics/">metrics</a>
            and how to understand
            <a href="https://rtd.allegrogroup.com/docs/chi/pl/latest/results/">p-Value</a>.

          </div>
        </chi-panel>

        <assignment-panel
          :experiment="experiment"
        ></assignment-panel>

      </v-flex>
    </v-layout>
  </v-container>
</template>

<script>
  import { mapState, mapActions } from 'vuex'

  import AssignmentPanel from './assignments/AssignmentPanel.vue'
  import ResultTable from './result/ResultTable.vue'
  import ExperimentDetails from './ExperimentDetails.vue'
  import ChiPanel from '../ChiPanel.vue'

  export default {
    mounted () {
      this.getExperiment({ params: { experimentId: this.$route.params.experimentId } })
    },

    computed: mapState({
      experiment: state => state.experiment.experiment,
      error: state => state.experiment.error.experiment,
      pending: state => state.experiment.pending.experiment
    }),

    components: {
      AssignmentPanel,
      ResultTable,
      ExperimentDetails,
      ChiPanel
    },

    methods: {
      ...mapActions(['getExperiment'])
    }
  }
</script>
