<template>
  <v-container>
    <v-layout>
      <v-flex md12 lg10 xl10 offset-xl1 offset-md1 offset-lg1>

        <h1>Experiment <span style="font-family: monospace">{{ $route.params.experimentId }}</span></h1>

        <experiment-details
          :experiment="experimentDefinition"
          :experimentStatistics="selectedExperimentStatistics"
          v-if="experimentDefinition.isReady()"
        ></experiment-details>

        <chi-panel title="Statistical hypothesis testing">
          <result-table
            @deviceChangedOnStats="onDeviceChanged"
            :experimentStatistics="selectedExperimentStatistics"
            :experimentStatisticsError="experimentStatistics.getError()"
            :experimentStatisticsPending="experimentStatistics.isPending()"
            v-if="experimentStatistics.isReady()"
            :experiment="experimentDefinition"
            :selectedDevice="selectedDevice"
          ></result-table>
        </chi-panel>

        <!--<chi-panel title="Bayesian analysis">-->
          <!--<bayesian-result-->
            <!--@deviceChangedOnBayesian="onDeviceChanged"-->
            <!--v-if="loadingExperimentDone && loadingBayesianResultDone"-->
            <!--:experiment="experiment"-->
            <!--:selectedDevice="selectedDevice"-->
            <!--:bayesianHistograms="bayesianHistograms"-->
            <!--:bayesianEqualizer="bayesianEqualizer"-->
          <!--&gt;</bayesian-result>-->
        <!--</chi-panel>-->

        <experiment-actions
          v-if="experimentDefinition.isReady() && experimentStatistics.isReady()"
          :experiment="experimentDefinition"
          :allowDelete="experimentDefinition.isReady() && experimentStatistics.isReady() && !experimentStatistics.any()"
        ></experiment-actions>

        <assignment-panel
          v-if="experimentDefinition.isReady() && experimentDefinition.status !== 'ENDED'"
          :experiment="experimentDefinition"
        ></assignment-panel>

        <audit-log-panel
          v-if="experimentDefinition.isReady()"
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
        console.log('Loaded experiment')
        this.selectedDevice = this.experimentDefinition.getInitialDevice()
        this.selectedExperimentStatistics = this.experimentStatistics.getForDevice(this.selectedDevice)
      })
    },

    data () {
      return {
        selectedDevice: null,
        selectedExperimentStatistics: null
      }
    },

    computed: mapState({
      experimentDefinition: state => state.experimentStore.experimentDefinition,
      experimentStatistics: state => state.experimentStore.experimentStatistics
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
        this.selectedExperimentStatistics = this.experimentStatistics.getForDevice(device.device)
      },

    }
  }
</script>
