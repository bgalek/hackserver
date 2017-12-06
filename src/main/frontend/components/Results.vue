<template>
  <div>
    <h2>Results
      <small>(Duration: {{experimentStatistics.durationDays}} days)</small>
    </h2>
    <v-container fluid>
      <v-layout row wrap>
        <v-flex xs2 md2>
          <v-card flat>
            <v-card-text>
              <v-menu>
                <v-text-field
                  slot="activator"
                  label="Results to day:"
                  v-model="statsRangeEnd"
                  prepend-icon="event"
                ></v-text-field>
                <v-date-picker v-model="statsRangeEnd" :allowed-dates="allowedDates" no-title scrollable actions>
                  <template slot-scope="{ save, cancel }">
                    <v-card-actions>
                      <v-spacer></v-spacer>
                      <v-btn flat color="primary" @click="cancel">Cancel</v-btn>
                      <v-btn flat color="primary" @click="save">OK</v-btn>
                    </v-card-actions>
                  </template>
                </v-date-picker>
              </v-menu>
            </v-card-text>
          </v-card>
        </v-flex>
        <v-flex xs10 md10>
          <v-card flat>
            <v-card-text>
              <v-radio-group v-model="device" row>
                <v-radio flat id="all" label="All" value="all"></v-radio>
                <v-radio flat id="desktop" label="Desktop" value="desktop"></v-radio>
                <v-radio flat id="smartphone" label="Smartphone" value="smartphone"></v-radio>
                <v-radio flat id="tablet" label="Tablet" value="tablet"></v-radio>
              </v-radio-group>
            </v-card-text>
          </v-card>
        </v-flex>
      </v-layout>
    </v-container>

    <div v-if="experimentStatistics.metrics">
      <div v-for="(variantsMetricValue, metric) in experimentStatistics.metrics" :key="metric">
        <h3>{{metricNames[metric]}}</h3>
        <v-data-table
          v-bind:headers="headers"
          :items="variantsMetricValue"
          hide-actions
          class="elevation-1"
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
  import Moment from 'moment'
  import Results from './Results.vue'
  import { extendMoment } from 'moment-range'

  const moment = extendMoment(Moment)

  export default {
    props: ['experimentId', 'initialStatsRangeEnd', 'initialDevice'],

    data () {
      return {
        headers: [
          {text: 'Variant'},
          {text: 'Value'},
          {text: 'Diff'},
          {text: 'P Value'},
          {text: 'Count'}
        ],
        metricNames: {
          'tx_visit': 'Transaction Per Visit',
          'gmv': 'GMV'
        },
        allowedDates: moment.range(new Date('2017-01-01'), new Date()),
        statsRangeEnd: this.initialStatsRangeEnd,
        device: this.initialDevice
      }
    },

    mounted () {
      this.mountExperimentStatistics()
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

      yesterday () {
        return moment().add(-1, 'day').format('YYYY-MM-DD')
      },

      mountExperimentStatistics (statsRangeEnd, device) {
        this.getExperimentStatistics({
          params: {
            experimentId: this.experimentId,
            device: device,
            toDate: statsRangeEnd
          }
        })
      }
    },

    watch: {
      device (device) {
        this.mountExperimentStatistics(this.statsRangeEnd, device)
        this.$emit('filtersChanged', {
          device: device,
          toDate: this.statsRangeEnd
        })
      },

      statsRangeEnd (date) {
        this.mountExperimentStatistics(date, this.device)
        this.$emit('filtersChanged', {
          device: this.device,
          toDate: date
        })
      }
    }
  }
</script>
