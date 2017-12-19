<template>

  <div>
    <p v-if="experimentStatistics.durationDays>0" style="margin-top: 10px;">
      Data calculated for
      <v-chip outline color="black">{{ experimentStatistics.durationDays }}</v-chip>
      days.
    </p>
    <div v-if="experimentStatistics.metrics">
      <div v-for="(variantsMetricValue, metric) in experimentStatistics.metrics" :key="metric">
        <b>{{metricNames[metric]}}</b>

        <pivot-link cube-type="metrics" :experiment-id="experiment.id"
                    :selected-metric-name="metricPivotNames[metric]"
        ></pivot-link>

        <v-data-table
          v-bind:headers="headers"
          :items="variantsMetricValue"
          hide-actions
          :custom-sort="sortVariantStats"
        >
          <template slot="items" slot-scope="props">
            <td>{{ props.item.variant }}</td>
            <td class="text-xs-right">{{ formatNumber(props.item.value) }}</td>

            <td class="text-xs-right">
              <v-tooltip top>
                <v-chip slot="activator" v-if="showVariant(props.item.variant)" label :color="diffColor(props.item).back" :text-color="diffColor(props.item).text">
                  {{ formatDiff(props.item) }}
                  <v-icon right>{{ diffIcon(props.item.diff) }}</v-icon>
                </v-chip>
                <span>{{ diffToolTip(props.item) }}</span>
              </v-tooltip>
            </td>

            <td class="text-xs-right"><div v-if="showVariant(props.item.variant)">{{ formatNumber(props.item.pValue) }}</div></td>
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
  import PivotLink from '../../PivotLink.vue'

  export default {
    props: ['experiment', 'toDate', 'device'],

    components: {
      PivotLink
    },

    data () {
      return {
        headers: [
          {text: 'Variant', sortable: false},
          {text: 'Metric Value', sortable: false},
          {text: 'Diff to Base', sortable: false},
          {text: 'p-Value', sortable: false},
          {text: 'Sample Count', sortable: false}
        ],
        metricPivotNames: {
          'tx_visit': '1000sum-06a',
          'gmv': 'define me'
        },
        metricNames: {
          'tx_visit': 'Visits With Transaction(s)',
          'gmv': 'GMV'
        }
      }
    },

    mounted () {
      this.mountExperimentStatistics(this.toDate, this.device)
    },

    computed: mapState({
      experimentStatistics: state => {
        const stats = state.experimentStatistics.experimentStatistics
        const mappedMetrics = {}

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
            experimentId: this.$route.params.experimentId,
            device,
            toDate
          }
        })
      },

      diffToolTip (metricVariant) {
        const testSignificance = this.testSignificance(metricVariant)

        if (testSignificance === 'no') {
          return 'The difference is statistically not significant.'
        }

        if (testSignificance === 'strong') {
          return 'The difference is strongly statistically significant (p-Value is less than 0.01).'
        }

        if (testSignificance === 'light') {
          return 'The difference is statistically significant (p-Value is less than 0.05).'
        }
      },

      diffColor (metricVariant) {
        const diff = metricVariant.diff

        const testSignificance = this.testSignificance(metricVariant)

        if (testSignificance === 'no') {
          return {
            text: 'black',
            back: 'white'
          }
        }

        const trendColor = diff > 0 ? 'green' : 'red'

        if (testSignificance === 'strong') {
          return {
            text: 'white',
            back: trendColor
          }
        }

        if (testSignificance === 'light') {
          return {
            text: 'black',
            back: trendColor + ' ' + 'lighten-4'
          }
        }
      },

      testSignificance (metricVariant) {
        const pVal = metricVariant.pValue

        if (pVal > 0.05) {
          return 'no'
        }

        // if p-Value < 0.01, we are sure about statistical significance
        if (pVal < 0.01) {
          return 'strong'
        }

        // if p-Value is between 0.01 and 0.05, we are not so sure about statistical significance
        return 'light'
      },

      diffIcon (diff) {
        if (diff > 0) {
          return 'trending_up'
        }
        if (diff < 0) {
          return 'trending_down'
        }
        return 'trending_flat'
      },

      showVariant (variant) {
        return variant !== 'base'
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
      },

      formatNumber (num) {
        const expThres = 0.0001

        if (!num) {
          return ''
        }

        const numAbs = Math.abs(num)

        if (numAbs > expThres && numAbs < 100) {
          return num.toFixed(4)
        }

        if (numAbs > 100) {
          return num.toFixed(2)
        }

        return num.toExponential(2)
      },

      formatPercent (num) {
        if (!num) {
          return ''
        }

        if (Math.abs(num) < 0.01) {
          return num.toExponential(2) + '%'
        }

        return num.toFixed(2) + '%'
      },

      formatDiff (metricVariant) {
        const variantValue = metricVariant.value
        const diff = metricVariant.diff
        const baseValue = variantValue - diff

        return this.formatPercent(100 * diff / baseValue, 0.01)
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
