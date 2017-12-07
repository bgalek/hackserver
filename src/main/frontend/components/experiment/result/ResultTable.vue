<template>
  <div>
    <h2>Results for {{experimentStatistics.durationDays}} days</h2>
    <div v-if="experimentStatistics.metrics">
      <div v-for="(variantsMetricValue, metric) in experimentStatistics.metrics" :key="metric">
        <h3>{{metricNames[metric]}}</h3>
        <v-data-table
          v-bind:headers="headers"
          :items="variantsMetricValue"
          hide-actions
          class="elevation-1"
          :custom-sort="sortVariantStats"
        >
          <template slot="items" slot-scope="props">
            <td>{{ props.item.variant }}</td>
            <td class="text-xs-right">{{ props.item.value }}</td>
            <td class="text-xs-right">{{ props.item.diff }}</td>
            <td class="text-xs-right">{{ props.item.pValue }}</td>
            <td class="text-xs-right">{{ props.item.count }}</td>
          </template>
        </v-data-table>
        <v-divider></v-divider>
      </div>
    </div>


    <v-alert v-if="experimentStatisticsError" color="error" icon="warning" value="true">
      Couldn't load experiment {{ $route.params.experimentId }} : {{ experimentStatisticsError.message }}
    </v-alert>

    <p class="text-xs-center">
      <v-progress-circular v-if="experimentStatisticsPending" indeterminate :size="70" :width="7"
                           color="purple"></v-progress-circular>
    </p>

  </div>

</template>

<script>
  import {mapState, mapActions} from 'vuex'
  import _ from 'lodash'

  export default {
    props: ['experimentId', 'toDate', 'device'],

    data () {
      return {
        headers: [
          {text: 'Variant', sortable: false},
          {text: 'Value', sortable: false},
          {text: 'Diff', sortable: false},
          {text: 'P Value', sortable: false},
          {text: 'Count', sortable: false}
        ],
        metricNames: {
          'tx_visit': 'Transaction Per Visit',
          'gmv': 'GMV'
        }
      }
    },

    mounted () {
      this.mountExperimentStatistics(this.toDate, this.device)
    },

    computed: mapState({
      experimentStatistics: state => {
        let stats = state.experimentStatistics.experimentStatistics
        let mappedMetrics = {}

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
          mappedMetrics[metricName] = mappedVariants
        })

        return {
          id: stats.id,
          durationDays: Math.floor(stats.duration / (3600 * 24 * 1000)),
          device: stats.device,
          toDate: stats.toDate,
          metrics: mappedMetrics
        }
      },
      experimentStatisticsError: state => state.experimentStatistics.error.experimentStatistics,
      experimentStatisticsPending: state => state.experimentStatistics.pending.experimentStatistics
    }),

    methods: {
      ...mapActions(['getExperimentStatistics']),

      mountExperimentStatistics (toDate, device) {
        this.getExperimentStatistics({
          params: {
            experimentId: this.experimentId,
            device,
            toDate
          }
        })
      },

      sortVariantStats (items) {
        items.sort((l, r) => {
          if (l.variant === 'base') {
            return -1
          }
          if (r.variant === 'base') {
            return 1
          }
          return r.diff - l.diff
        })
        return items
      }
    },

    watch: {
      device (device) {
        this.mountExperimentStatistics(this.toDate, device)
      },

      toDate (date) {
        this.mountExperimentStatistics(date, this.device)
      }
    }
  }
</script>
