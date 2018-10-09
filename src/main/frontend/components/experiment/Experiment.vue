<template>
  <v-container>
    <v-layout>
      <v-flex md12 lg10 xl10 offset-xl1 offset-md1 offset-lg1>

        <h1>Experiment <span style="font-family: monospace">{{ $route.params.experimentId }}</span></h1>

        <experiment-details
          :experiment="experiment"
          v-if="loadingExperimentDone"
        ></experiment-details>

        <chi-panel title="Statistical hypothesis testing">
          <result-table
            @deviceChangedOnStats="onDeviceChanged"
            :experimentStatistics="experimentStatistics"
            :experimentStatisticsError="experimentStatisticsError"
            :experimentStatisticsPending="experimentStatisticsPending"
            v-if="loadingExperimentDone"
            :experiment="experiment"
            :selectedDevice="selectedDevice"
          ></result-table>
        </chi-panel>

        <chi-panel title="Bayesian analysis">
          <bayesian-result
            @deviceChangedOnBayesian="onDeviceChanged"
            v-if="loadingExperimentDone && loadingBayesianResultDone"
            :experiment="experiment"
            :selectedDevice="selectedDevice"
            :bayesianHistograms="bayesianHistograms"
            :bayesianEqualizer="bayesianEqualizer"
          ></bayesian-result>
        </chi-panel>

        <experiment-actions
          v-if="loadingExperimentDone && loadingStatsDone"
          :experiment="experiment"
          :allowDelete="metricsNotAvailable"
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
  import BayesianResult from './result/BayesianResult.vue'
  import ExperimentDetails from './ExperimentDetails.vue'
  import ExperimentActions from './ExperimentActions.vue'
  import ChiPanel from '../ChiPanel.vue'
  import AuditLogPanel from './audit/AuditLogPanel'
  import _ from 'lodash'

  export default {
    mounted () {
      this.getExperiment({ params: { experimentId: this.$route.params.experimentId } }).then(() => {
        this.loadingExperimentDone = true
        this.selectedDevice = this.calcInitialDevice(this.experiment)
      })

      this.loadExperimentStatistics('all', this.$route.params.experimentId).then(() => {
        this.metricsNotAvailable = this.experimentStatistics.metrics.length === 0
        console.log('loading stats for "' + this.experimentId + '" done')
        this.loadingStatsDone = true
      }).catch(() => {
        this.metricsNotAvailable = true
        this.loadingStatsDone = true
      })

      this.loadExperimentBayesianResult('all', this.$route.params.experimentId).then(() => {
        this.loadingBayesianResultDone = true
      }).catch(() => {
        this.loadingBayesianResultDone = true
      })

      this.loadExperimentBayesianEqualizerResult('all', this.$route.params.experimentId).then(() => {
        this.loadingBayesianEqualizerResultDone = true
      }).catch(() => {
        this.loadingBayesianEqualizerResultDone = true
      })
    },

    data () {
      return {
        selectedDevice: '',
        loadingStatsDone: false,
        loadingBayesianResultDone: false,
        loadingBayesianEqualizerResultDone: false,
        loadingExperimentDone: false,
        metricOrder: {
          'tx_visit': 1,
          'tx_avg': 2,
          'gmv': 3,
          'tx_daily': 4,
          'tx_avg_daily': 5,
          'gmv_daily': 6
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
      isInGroup: state => {
        return state.experiment.experiment.isInGroup()
      },
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
          metricsNotAvailable: stats.metrics && stats.metrics.length === 0
        }
      },

      bayesianHistograms (state) {
        return state.bayesianHistograms.bayesianHistograms || { }
      },

      bayesianEqualizer (state) {
        return state.bayesianEqualizer.bayesianEqualizer || { }
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
      ExperimentActions,
      BayesianResult
    },

    methods: {
      ...mapActions(['getExperiment', 'getExperimentStatistics', 'getBayesianHistograms', 'getBayesianEqualizer']),

      loadExperimentStatistics (device, experimentId) {
        return this.getExperimentStatistics({
          params: {
            experimentId,
            device
          }
        })
      },

      loadExperimentBayesianResult (device, experimentId) {
        return this.getBayesianHistograms({
          params: {
            experimentId,
            device
          }
        })
      },

      loadExperimentBayesianEqualizerResult (device, experimentId) {
        return this.getBayesianEqualizer({
          params: {
            experimentId,
            device
          }
        })
      },

      onDeviceChanged (device) {
        this.loadExperimentStatistics(device.device, this.experiment.id)
        this.loadExperimentBayesianResult(device.device, this.experiment.id)
        this.loadExperimentBayesianEqualizerResult(device.device, this.experiment.id)

        this.selectedDevice = device.device
      },

      calcInitialDevice (experiment) {
        const baseClass = experiment.getBaseDeviceClass()

        if (baseClass === 'desktop') {
          return baseClass
        }

        if (baseClass && baseClass.startsWith('phone')) {
          return 'smartphone'
        }

        return 'all'
      }
    }
  }
</script>
