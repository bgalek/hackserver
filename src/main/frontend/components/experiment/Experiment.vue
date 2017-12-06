<template>
  <v-container>
    <v-layout>
      <v-flex offset-md1 md10 lg9 offset-xl2 xl8>

        <h1>Experiment: {{ $route.params.experimentId }}</h1>
        <result-table-settings
          :initialDevice="device"
          :initialToDate="toDate"
          v-on:settingsChanged="updateQueryParams"
        ></result-table-settings>
        <result-table
          :experiment-id="$route.params.experimentId"
          v-bind:device="device"
          v-bind:toDate="toDate"
        ></result-table>
        <assignment-panel :experiment-id="$route.params.experimentId"></assignment-panel>

      </v-flex>
    </v-layout>
  </v-container>
</template>

<script>
  import AssignmentPanel from './assignments/AssignmentPanel.vue'
  import ResultTable from './result/ResultTable.vue'
  import ResultTableSettings from './result/ResultTableSettings.vue'
  import moment from 'moment'

  export default {
    data () {
      let deviceQueryParam = this.$route.query.device
      let toDateQueryParam = this.$route.query.toDate
      let defaultToDate = moment().add(-1, 'days').format('YYYY-MM-DD')
      return {
        device: deviceQueryParam || 'all',
        toDate: toDateQueryParam || defaultToDate
      }
    },

    components: {
      AssignmentPanel,
      ResultTable,
      ResultTableSettings
    },

    methods: {
      updateQueryParams ({device, toDate}) {
        this.device = device
        this.toDate = toDate
        this.$router.push({
          name: 'experiment',
          params: {experimentId: this.$route.params.experimentId},
          query: {device, toDate}
        })
      }
    }
  }
</script>
