<template>
  <v-container>
    <v-layout>
      <v-flex md12 lg10 xl10 offset-xl1 offset-md1 offset-lg1>

        <h1>Experiment <span style="font-family: monospace">{{ $route.params.experimentId }}</span></h1>

        <experiment-details
          :experiment="experimentDefinition"
          :experimentStatistics="selectedExperimentStatistics"
          v-if="experimentReady"
        ></experiment-details>

        <chi-panel title="Statistical hypothesis testing">
          <result-table
            @deviceChangedOnStats="onDeviceChanged"
            :experimentStatistics="selectedExperimentStatistics"
            :experimentStatisticsError="experimentError"
            :experimentStatisticsPending="!experimentReady"
            v-if="resultsReady()"
            :experiment="experimentDefinition"
            :selectedDevice="selectedDevice"
          ></result-table>
        </chi-panel>

        <chi-panel title="Bayesian analysis">
          <bayesian-result
            @deviceChangedOnBayesian="onDeviceChanged"
            v-if="bayesianResultsReady()"
            :experiment="experimentDefinition"
            :selectedDevice="selectedDevice"
            :bayesianHistograms="selectedExperimentBayesianHistograms"
            :bayesianEqualizer="selectedExperimentBayesianEqualizers"
            :histogramsToDate="histogramsToDate"
            :initialVariantName="initialVariantName"
          ></bayesian-result>
        </chi-panel>

        <experiment-actions
          v-if="experimentReady"
          :experiment="experimentDefinition"
          :allowDelete="experimentReady && !experimentStatistics.any()"
        ></experiment-actions>

        <assignment-panel
          v-if="experimentReady && experimentDefinition.status !== 'ENDED'"
          :experiment="experimentDefinition"
        ></assignment-panel>

        <audit-log-panel
          v-if="experimentReady"
          :experiment="experimentDefinition"
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

  export default {
    mounted () {
      this.getExperiment(this.$route.params.experimentId).then(() => {
        this.selectedDevice = this.experimentDefinition.getInitialDevice()
        this.refreshStatistics(this.experimentDefinition.getInitialDevice())
      })
    },

    data () {
      return {
        selectedDevice: null,
        selectedExperimentStatistics: null,
        selectedExperimentBayesianHistograms: null,
        selectedExperimentBayesianEqualizers: null
      }
    },

    computed: mapState({
      experimentDefinition: state => state.experimentStore.experimentDefinition,
      experimentStatistics: state => state.experimentStore.experimentStatistics,
      experimentBayesianHistograms: state => state.experimentStore.experimentBayesianHistograms,
      histogramsToDate: state => {
        if (state.experimentStore.experimentBayesianHistograms && state.experimentStore.experimentDefinition) {
          const initialDevice = state.experimentStore.experimentDefinition.getInitialDevice()
          const histograms = state.experimentStore.experimentBayesianHistograms
            .getForDevice(initialDevice)
          console.log(histograms)
          return histograms.size > 0 ? histograms.metadata.toDate : null
        } else {
          return null
        }
      },

      initialVariantName: state => {
        if (state.experimentStore.experimentDefinition) {
          return state.experimentStore.experimentDefinition.getFirstVariant().name
        } else {
          return null
        }
      },
      experimentBayesianEqualizers: state => state.experimentStore.experimentBayesianEqualizers,
      experimentReady: state => state.experimentStore.experimentReady,
      experimentError: state => state.experimentStore.experimentError
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
      ...mapActions(['getExperiment']),

      onDeviceChanged (device) {
        this.selectedDevice = device.device
        this.refreshStatistics(this.selectedDevice)
      },

      refreshStatistics (device) {
        this.selectedExperimentStatistics = this.experimentStatistics.getForDevice(device)
        this.selectedExperimentBayesianHistograms = this.experimentBayesianHistograms.getForDevice(device)
        this.selectedExperimentBayesianEqualizers = this.experimentBayesianEqualizers.getForDevice(device)
      },

      bayesianResultsReady () {
        return this.experimentReady && this.selectedExperimentBayesianHistograms && this.selectedExperimentBayesianEqualizers
      },

      resultsReady () {
        return this.experimentReady && this.selectedExperimentStatistics
      }
    }
  }
</script>
