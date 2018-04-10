<template>
  <v-container>
    <v-layout>
      <v-flex md12 lg10 xl10 offset-xl1 offset-md1 offset-lg1>

        <h1>Experiment <span style="font-family: monospace">{{ $route.params.experimentId }}</span></h1>

        <experiment-details
          :experiment="experiment"
          v-if="loadingExperimentDone"
        ></experiment-details>

        <chi-panel title="Metrics & Statistics">
          <result-table
            @deviceChanged="onDeviceChanged"
            :experimentStatistics="experimentStatistics"
            :experimentStatisticsError="experimentStatisticsError"
            :experimentStatisticsPending="experimentStatisticsPending"
            v-if="loadingExperimentDone"
            :experiment="experiment"
          ></result-table>
          <div slot="footer">
            Read the χ Docs about <a href="https://rtd.allegrogroup.com/docs/chi/pl/latest/chi_metrics/">metrics</a>
            and how to understand
            <a href="https://rtd.allegrogroup.com/docs/chi/pl/latest/results/">p-Value</a>.
          </div>
        </chi-panel>

        <experiment-actions
          v-if="loadingExperimentDone && loadingStatsDone"
          :experiment="experiment"
          :allowDelete="allowDelete"
        ></experiment-actions>

        <assignment-panel
          v-if="loadingExperimentDone && experiment.status !== 'ENDED'"
          :experiment="experiment"
        ></assignment-panel>

        <audit-log-panel
          v-if="loadingExperimentDone"
          :experiment=experiment
        ></audit-log-panel>

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
  import AuditLogPanel from './audit/AuditLogPanel'
  import _ from 'lodash'

  export default {
    mounted () {
      this.getExperiment({ params: { experimentId: this.$route.params.experimentId } }).then(() => {
        this.loadingExperimentDone = true
      })

      this.loadExperimentStatistics('all', this.$route.params.experimentId).then(() => {
        this.allowDelete = this.experimentStatistics.metrics.length === 0
        console.log('loading stats for "' + this.experimentId + '" done')
        this.loadingStatsDone = true
      }).catch(() => {
        this.allowDelete = false
        this.loadingStatsDone = true
      })
    },

    data () {
      return {
        loadingStatsDone: false,
        loadingExperimentDone: false,
        device: 'all',
        metricOrder: {
          'tx_visit': 1,
          'tx_avg': 2,
          'gmv': 3
        }
      }
    },

    beforeRouteUpdate () {
      this.$router.go(this.$router.currentRoute)
    },

    computed: mapState({
      experiment: state => state.experiment.experiment,
      error: state => state.experiment.error.experiment,
      pending: state => state.experiment.pending.experiment,
      experimentId: state => state.experiment.experiment.id,

      experimentStatistics (state) {
        const stats = state.experimentStatistics.experimentStatistics

        const mappedMetrics = []

        _.forIn(stats.metrics, (metricValuePerVariant, metricName) => {
          let mappedVariants = []
          _.forIn(metricValuePerVariant, (metricValue, variantName) => {
            mappedVariants[(variantName !== 'base') ? 'push' : 'unshift']({
              variant: variantName,
              value: metricValue.value,
              diff: metricValue.diff,
              count: metricValue.count,
              pValue: metricValue.pValue
            })
          })
          mappedMetrics.push({
            'key': metricName,
            'variants': mappedVariants,
            'order': this.metricOrder[metricName]
          })
        })

        return {
          id: stats.id,
          durationDays: Math.floor(stats.duration / (3600 * 24 * 1000)),
          device: stats.device,
          toDate: stats.toDate,
          metrics: mappedMetrics.sort((x, y) => x.order - y.order),
          allowDelete: stats.metrics && stats.metrics.length === 0
        }
      },

      experimentStatisticsError: state => state.experimentStatistics.error.experimentStatistics,
      experimentStatisticsPending: state => state.experimentStatistics.pending.experimentStatistics
    }),

    components: {
      AssignmentPanel,
      AuditLogPanel,
      ResultTable,
      ExperimentDetails,
      ChiPanel,
      ExperimentActions
    },

    methods: {
      ...mapActions(['getExperiment', 'getExperimentStatistics']),

      loadExperimentStatistics (device, experimentId) {
        return this.getExperimentStatistics({
          params: {
            experimentId,
            device
          }
        })
      },

      onDeviceChanged ({device}) {
        this.loadExperimentStatistics(device, this.experiment.id)
      }
    }
  }
</script>
