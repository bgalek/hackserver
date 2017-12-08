<template>
  <v-container>
    <v-layout>
      <v-flex offset-md1 md10 lg9 offset-xl2 xl8>

        <h1>Experiment: {{ $route.params.experimentId }}</h1>

        <experiment-details
          :experiment="experiment"
        ></experiment-details>

        <result-table-settings
          v-if="experiment.reportingEnabled"
          :initialDevice="device"
          :initialToDate="toDate"
          v-on:settingsChanged="updateQueryParams"
        ></result-table-settings>

        <result-table
          v-if="experiment.reportingEnabled"
          :experiment="experiment"
          v-bind:device="device"
          v-bind:toDate="toDate"
        ></result-table>

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
  import ResultTableSettings from './result/ResultTableSettings.vue'
  import ExperimentDetails from './ExperimentDetails.vue'

  import moment from 'moment'

  export default {
    mounted () {
      this.getExperiment({ params: { experimentId: this.$route.params.experimentId } })
    },

    data () {
      let deviceQueryParam = this.$route.query.device
      let toDateQueryParam = this.$route.query.toDate
      let defaultToDate = moment().add(-1, 'days').format('YYYY-MM-DD')

      return {
        device: deviceQueryParam || 'all',
        toDate: toDateQueryParam || defaultToDate
      }
    },
    computed: mapState({
      experiment: state => state.experiment.experiment,
      error: state => state.experiment.error.experiment,
      pending: state => state.experiment.pending.experiment
    }),

    components: {
      AssignmentPanel,
      ResultTable,
      ResultTableSettings,
      ExperimentDetails
    },

    methods: {
      ...mapActions(['getExperiment']),

      updateQueryParams ({device, toDate}) {
        this.device = device
        this.toDate = toDate
        this.$router.push({
          name: 'experiment',
          params: { experimentId: this.$route.params.experimentId },
          query: {device, toDate}
        })
      }
    }
  }
</script>
