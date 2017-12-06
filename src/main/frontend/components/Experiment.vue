<template>
  <v-container>
    <v-layout>
      <v-flex offset-md1 md10 lg9 offset-xl2 xl8>

        <h1>Experiment: {{ $route.params.experimentId }}</h1>
        <results
          :experiment-id="$route.params.experimentId"
          :initialDevice="initialDevice"
          :initialStatsRangeEnd="initialStatsRangeEnd"
          v-on:filtersChanged="updateQueryParams"
        ></results>
        <assignment-panel :experiment-id="$route.params.experimentId"></assignment-panel>

      </v-flex>
    </v-layout>
  </v-container>
</template>

<script>
  import AssignmentPanel from './AssignmentPanel.vue'
  import Results from './Results.vue'
  import moment from 'moment'

  export default {
    data () {
      let deviceQueryParam = this.$route.query.device
      let toDateQueryParam = this.$route.query.toDate
      let defaultToDate = moment().add(-1, 'days').format('YYYY-MM-DD')
      return {
        initialDevice: deviceQueryParam || 'all',
        initialStatsRangeEnd: toDateQueryParam || defaultToDate
      }
    },

    components: {
      AssignmentPanel,
      Results
    },

    methods: {
      updateQueryParams ({device, toDate}) {
        this.$router.push({
          name: 'experiment',
          params: {experimentId: this.$route.params.experimentId},
          query: {device: device, toDate: toDate}
        })
      }
    }
  }
</script>
