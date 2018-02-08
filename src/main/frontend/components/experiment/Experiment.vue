<template>
  <v-container>
    <v-layout>
      <v-flex md12 lg10 offset-xl1 xl10>

        <h1>Experiment <span style="font-family: monospace">{{ $route.params.experimentId }}</span></h1>

        <experiment-details
          :experiment="experiment"
        ></experiment-details>

        <chi-panel title="Metrics & Statistics">
          <result-table
            v-if="experiment.reportingEnabled && experimentId"
            :experiment="experiment"
          ></result-table>

          <div slot="footer">
            Read the χ Docs about <a href="https://rtd.allegrogroup.com/docs/chi/pl/latest/chi_metrics/">metrics</a>
            and how to understand
            <a href="https://rtd.allegrogroup.com/docs/chi/pl/latest/results/">p-Value</a>.

          </div>
        </chi-panel>

        <experiment-actions
          :experiment="experiment"
        ></experiment-actions>

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
  import ExperimentActions from './ExperimentActions.vue'
  import ChiPanel from '../ChiPanel.vue'

  export default {
    mounted () {
      this.getExperiment({ params: { experimentId: this.$route.params.experimentId } })
    },

    computed: mapState({
      experiment: state => state.experiment.experiment,
      error: state => state.experiment.error.experiment,
      pending: state => state.experiment.pending.experiment,
      experimentId: state => state.experiment.experiment.id
    }),

    components: {
      AssignmentPanel,
      ResultTable,
      ExperimentDetails,
      ChiPanel,
      ExperimentActions
    },

    methods: {
      ...mapActions(['getExperiment'])
    }
  }
</script>
