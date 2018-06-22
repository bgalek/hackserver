<template>
  <v-container>

    <device-selector
      @deviceChanged="deviceChanged"
      :selectedDevice="selectedDevice"
    ></device-selector>

    <p v-if="experimentStatistics.durationDays > 0">
          Data calculated on
          <span id="toDate">{{ experimentStatistics.toDate }}</span>
          for
          <b>{{ experimentStatistics.durationDays }}</b> days.
    </p>

    <div v-if="experimentStatistics.metrics">
      <div v-for="metric in metrics()" :key="metric.order">

        <div style="margin-top: 30px;">
          <b>{{metricNames[metric.key]}}</b>

          <turnilo-link cube-type="metrics" :experiment-id="experiment.id" v-if="showMetricTurniloLink(metric.key)"
                      :selected-metric-name="metric.key"
          ></turnilo-link>
        </div>

        <v-data-table
          v-bind:headers="headers"
          :items="metric.variants"
          hide-actions
          :custom-sort="sortVariantStats"
        >
          <template slot="items" slot-scope="props">
            <td>{{ props.item.variant }}</td>
            <td class="text-xs-right">{{ metricFormatter[metric.key](props.item.value) }}</td>

            <td class="text-xs-right">
              <v-tooltip top>
                <v-chip slot="activator" v-if="showVariant(props.item.variant)" label
                        :color="diffColor(props.item).back"
                        :outline="diffColor(props.item).outline"
                        :text-color="diffColor(props.item).text"
                >
                  {{ formatDiff(props.item) }}
                  <v-icon right>{{ diffIcon(props.item.diff) }}</v-icon>
                </v-chip>
                <div>{{ diffToolTip(props.item) }}</div>
              </v-tooltip>
            </td>

            <td class="text-xs-right">
              <div v-if="showVariant(props.item.variant)">
                {{ formatNumber(props.item.pValue, 4) }}
                <turnilo-link cube-type="stats" :experiment-id="experiment.id"
                            selected-metric-name="p_value" :variant="props.item.variant"
                            :metric="metric.key"
                ></turnilo-link>
              </div>
            </td>

            <td class="text-xs-right">{{ formatCount(props.item.count) }}</td>
          </template>
          <hr/>

        </v-data-table>
        <v-divider></v-divider>
      </div>
    </div>

    <v-alert v-if="experimentStatisticsError" color="error" icon="warning" value="true">
      Couldn't load statistics for {{ this.experiment.id }} : {{ experimentStatisticsError.message }}
    </v-alert>

    <p class="text-xs-center">
      <v-progress-circular v-if="experimentStatisticsPending" indeterminate :size="70" :width="7"
                           color="purple"></v-progress-circular>
    </p>

    <div slot="footer">
      Read the χ Docs about <a href="https://rtd.allegrogroup.com/docs/chi/pl/latest/chi_metrics/">metrics</a>
      and how to understand
      <a href="https://rtd.allegrogroup.com/docs/chi/pl/latest/results/">p-Value</a>.
    </div>

  </v-container>

</template>

<script>
  import {mapActions} from 'vuex'
  import TurniloLink from '../../TurniloLink.vue'
  import DeviceSelector from './DeviceSelector'

  export default {
    props: ['experiment', 'experimentStatistics', 'experimentStatisticsError', 'experimentStatisticsPending', 'selectedDevice'],

    components: {
      TurniloLink, DeviceSelector
    },

    data () {
      return {
        strongAlfaThres: 0.01,
        lightAlfaThres: 0.05,
        headers: [
          {text: 'Variant', sortable: false, align: 'left'},
          {text: 'Metric Value', sortable: false, align: 'right'},
          {text: 'Diff to Base', sortable: false, align: 'center'},
          {text: 'p-Value', sortable: false, align: 'center'},
          {text: 'Sample Count', sortable: false, align: 'right'}
        ],
        hiddenMetrics: ['tx_avg', 'tx_avg_daily'],
        metricNames: {
          'tx_visit': 'Visits conversion',
          'tx_daily': 'Daily conversion - BETA',
          'tx_avg': 'Transactions per visit',
          'tx_avg_daily': 'Transactions daily - BETA',
          'gmv': 'GMV per visit',
          'gmv_daily': 'GMV daily - BETA'
        },
        metricFormatter: {
          'tx_visit': (it) => this.formatAsPercent(it),
          'tx_daily': (it) => this.formatAsPercent(it),
          'tx_avg': (it) => this.formatNumber(it, 4),
          'tx_avg_daily': (it) => this.formatNumber(it, 4),
          'gmv': (it) => this.formatCurrency(it, 'PLN'),
          'gmv_daily': (it) => this.formatCurrency(it, 'PLN')
        },
        hiddenMetricsTurnilo: ['tx_daily', 'tx_avg_daily', 'gmv_daily']
      }
    },

    methods: {
      ...mapActions(['getExperimentStatistics']),

      diffToolTip (metricVariant) {
        const testSignificance = this.testSignificance(metricVariant)

        if (testSignificance === 'wait') {
          return 'Wait till the end of the experiment...'
        }

        if (testSignificance === 'no') {
          return 'The difference is statistically not significant.'
        }

        if (testSignificance === 'strong') {
          return `The difference is statistically significant with 𝜶 = ${this.experiment.desiredAlpha()}.`
        }

        if (testSignificance === 'light') {
          return  `Chi needs to adjust the ` +
                  `significance level 𝜶 to ${this.experiment.usedAlpha()} ` +
                  `which makes this individual test statistically insignificant.`
        }

        if (testSignificance === 'promising') {
          return 'Okay, looks like the experiment is going to become statistically significant. ' +
                 'Wait till the end of the experiment for the official results. '
        }
      },

      showMetricTurniloLink (metricKey) {
        return !this.hiddenMetricsTurnilo.includes(metricKey)
      },

      testSignificance (metricVariant) {
        const pVal = metricVariant.pValue

        // if p-Value > 0.05
        if (pVal > this.experiment.desiredAlpha()) {
          if (this.experiment.status === 'ENDED') {
            return 'no'
          } else {
            return 'wait'
          }
        }

        // if p-Value < 0.05 and experiment is not ENDED
        if (pVal < this.experiment.desiredAlpha() && this.experiment.status !== 'ENDED') {
          return 'promising'
        }

        // if p-Value is < 0.05 but > 0.05*Bonferroni
        if (pVal > this.experiment.usedAlpha() && pVal < this.experiment.desiredAlpha() &&
            this.experiment.status === 'ENDED') {
          return 'light'
        }

        // if p-Value < 0.05*Bonferroni, we are sure about statistical significance
        if (pVal <= this.experiment.usedAlpha() && this.experiment.status === 'ENDED') {
          return 'strong'
        }
      },

      metrics () {
        return this.experimentStatistics.metrics &&
               this.experimentStatistics.metrics.filter(metric => !this.hiddenMetrics.includes(metric.key))
      },

      diffColor (metricVariant) {
        const diff = metricVariant.diff

        const testSignificance = this.testSignificance(metricVariant)

        const trendColor = diff > 0 ? 'green' : 'red'

        if (testSignificance === 'no') {
          return {
            text: 'black',
            back: 'gray-lighten-4'
          }
        }

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

        if (testSignificance === 'promising') {
          return {
            text: 'black',
            back: trendColor + ' ' + 'lighten-4',
            outline: true
          }
        }

        if (testSignificance === 'wait') {
          return {
            text: 'black',
            back: 'white'
          }
        }
      },

      diffIcon (diff) {
        if (diff > 0) {
          return 'arrow_upward'
        }
        if (diff < 0) {
          return 'arrow_downward'
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
          return (l.variant < r.variant) ? -1 : (l.variant > r.variant) ? 1 : 0
        })
        return items
      },

      formatCount (num) {
        return num.toLocaleString('pl')
      },

      formatCurrency (num, symbol) {
        return this.formatNumber(num, 4) + ' ' + symbol
      },

      formatNumber (num, decimals) {
        if (!num) {
          return ''
        }

        const expThres = 0.0001

        const numAbs = Math.abs(num)

        if (numAbs > expThres && numAbs < 100) {
          return num.toFixed(decimals)
        }

        if (numAbs > 100) {
          return num.toFixed(2)
        }

        return num.toExponential(2)
      },

      formatAsPercent (num) {
        if (!num) {
          return ''
        }

        const numPercent = num * 100

        if (Math.abs(numPercent) < 0.01) {
          return numPercent.toExponential(2) + ' %'
        }

        return numPercent.toFixed(2) + ' %'
      },

      formatDiff (metricVariant) {
        const variantValue = metricVariant.value
        const diff = metricVariant.diff
        const baseValue = variantValue - diff

        return this.formatAsPercent(diff / baseValue)
      },

      deviceChanged ({device}) {
        this.$emit('deviceChangedOnStats', {
          device: device
        })
      }
    }
  }
</script>

<style scoped>
  #toDate {
    font-weight: bold;
  }

  >>>.datatable thead tr {
    height: auto;
  }
</style>
