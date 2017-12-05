<template>
  <v-container>
    <v-layout>
      <v-flex offset-md1 md10 lg9 offset-xl2 xl8>
        <h1>Experiment: {{ $route.params.experimentId }}</h1>

        <h2>Results <small>(Duration: {{experimentStatistics.durationDays}} days)</small></h2>
        <v-container fluid>
          <v-layout row wrap>
            <v-flex xs2 md2>
              <v-card flat>
                <v-card-text>
                  <v-menu>
                    <v-text-field
                      slot="activator"
                      label="From day:"
                      v-model="pickedDate"
                      prepend-icon="event"
                    ></v-text-field>
                    <v-date-picker v-model="pickedDate" :allowed-dates="allowedDates" no-title scrollable actions>
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
          <v-progress-circular v-if="experimentStatisticsPending" indeterminate :size="70" :width="7" color="purple"></v-progress-circular>
        </p>

        <h2>Assignments</h2>
        <assignment-panel :experiment-id="$route.params.experimentId"></assignment-panel>

      </v-flex>
    </v-layout>
  </v-container>
</template>

<script>
  import {mapState, mapActions} from 'vuex'
  import _ from 'lodash'
  import Moment from 'moment'
  import AssignmentPanel from './AssignmentPanel.vue'
  import { extendMoment } from 'moment-range'

  const moment = extendMoment(Moment)

  export default {
    data () {
      let device = 'all'
      let pickedDate = this.yesterday()
      if (this.$route.query.device) {
        device = this.$route.query.device
      }
      if (this.$route.query.toDate) {
        pickedDate = this.$route.query.toDate
      }

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
        device: device,
        pickedDate: pickedDate,
        allowedDates: moment.range(new Date('2017-01-01'), new Date())
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

      filterByDevice (device) {
        this.$router.push({
          name: 'experiment',
          params: {experimentId: this.$route.params.experimentId},
          query: {device: device, toDate: this.pickedDate}
        })
        this.mountExperimentStatistics()
      },

      filterByDate (date) {
        this.$router.push({
          name: 'experiment',
          params: {experimentId: this.$route.params.experimentId},
          query: {device: this.device, toDate: date}
        })
        this.mountExperimentStatistics()
      },

      mountExperimentStatistics () {
        let params = {
          experimentId: this.$route.params.experimentId
        }
        if (this.$route.query.device) {
          params.device = this.$route.query.device
        }
        if (this.$route.query.toDate) {
          params.toDate = this.$route.query.toDate
        }
        this.getExperimentStatistics({
          params: params
        })
      }
    },

    watch: {
      device (device) {
        this.filterByDevice(device)
      },

      pickedDate (date) {
        this.filterByDate(date)
      }
    },

    components: {
      AssignmentPanel
    }
  }
</script>
