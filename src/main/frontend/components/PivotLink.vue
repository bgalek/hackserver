<template>
  <v-btn icon @click="goToPivot()" style="margin-bottom: 10px; margin-top: 0px;">
    <v-icon>fa-bar-chart</v-icon>
  </v-btn>
</template>
<script>
  import axios from 'axios'

  export default {
    props: {
      selectedMetricName: String,
      metric: String,
      cubeType: String,
      experimentId: String,
      variant: String
    },

    methods: {
      isProdEnv () {
        return window.location.hostname === 'chi.allegrogroup.com'
      },

      pivotMetricCode () {
        const metric = this.selectedMetricName

        const names = {
          'tx_visit': 'visit_conversion',
          'tx_avg': 'transactions_per_100_visits',
          'p_value': 'p_value',
          'gmv': 'gmv_per_visit'
        }
        return names[metric]
      },

      pivotUrl () {
        const PIVOT_PROD = 'https://turnilo.allegrogroup.com'
        const PIVOT_TEST = 'https://turnilo-nga-test.allegrogroup.com'

        return this.isProdEnv() ? PIVOT_PROD : PIVOT_TEST
      },

      buildPivotRequest () {
        if (this.cubeType === 'metrics') {
          return {
            dataCubeName: 'chi_experiments',
            viewDefinitionVersion: '3',
            viewDefinition: {
              visualization: 'line-chart',
              timezone: 'Etc/UTC',
              filters: [
                {
                  type: 'time',
                  ref: '__time',
                  timePeriods: [
                    {
                      duration: 'P1D',
                      type: 'latest',
                      step: -7
                    }
                  ]
                },
                {
                  type: 'string',
                  ref: 'experiment_id',
                  action: 'in',
                  values: [
                    this.experimentId
                  ],
                  not: false
                }
              ],
              splits: [
                {
                  type: 'string',
                  dimension: 'experiment_variant',
                  sort: {
                    ref: this.pivotMetricCode(),
                    direction: 'descending'
                  },
                  limit: null
                },
                {
                  type: 'time',
                  dimension: '__time',
                  granularity: 'PT1H',
                  sort: {
                    ref: '__time',
                    direction: 'ascending'
                  },
                  limit: null
                }
              ],
              measures: {
                isMulti: false,
                single: this.pivotMetricCode(),
                multi: [
                  'transaction_count',
                  'transaction_sum',
                  'visit_count',
                  'visit_with_tx_count'
                ]
              },
              pinnedDimensions: [],
              pinnedSort: this.pivotMetricCode(),
              legend: {
                dimension: 'experiment_variant',
                limit: 5
              },
              highlight: null
            }
          }
        }
        if (this.cubeType === 'stats') {
          return {
            dataCubeName: 'chi_experiments_stats',
            viewDefinitionVersion: '3',
            viewDefinition: {
              visualization: 'line-chart',
              timezone: 'Etc/UTC',
              filters: [
                {
                  type: 'string',
                  ref: 'experiment_id',
                  action: 'in',
                  values: [
                    this.experimentId
                  ],
                  not: false
                },
                {
                  type: 'string',
                  ref: 'experiment_variant',
                  action: 'in',
                  values: [
                    this.variant
                  ],
                  not: false
                },
                {
                  type: 'string',
                  ref: 'metric',
                  action: 'in',
                  values: [
                    this.metric
                  ],
                  not: false
                },
                {
                  type: 'time',
                  ref: '__time',
                  timePeriods: [
                    {
                      duration: 'P1D',
                      type: 'latest',
                      step: -7
                    }
                  ]
                }
              ],
              splits: [
                {
                  type: 'string',
                  dimension: 'device_class',
                  sort: {
                    ref: 'p_value',
                    direction: 'descending'
                  },
                  limit: 50
                },
                {
                  type: 'time',
                  dimension: '__time',
                  granularity: 'P1D',
                  sort: {
                    ref: '__time',
                    direction: 'ascending'
                  },
                  limit: null
                }
              ],
              measures: {
                isMulti: false,
                single: this.pivotMetricCode(),
                multi: [
                  'count',
                  'experiment_duration',
                  'metric_value',
                  'metric_value_diff'
                ]
              },
              pinnedDimensions: [],
              pinnedSort: 'p_value',
              legend: {
                dimension: 'device_class',
                limit: 5
              },
              highlight: null
            }
          }
        }
      },

      goToPivot () {
        axios.post(`${this.pivotUrl()}/mkurl`, this.buildPivotRequest()).then(response => {
          window.open(this.pivotUrl() + '/' + response.data.hash, '_blank')
        }).catch(error => {
          console.log('failed to generate Pivot link', error.message)
        })
      }
    }
  }
</script>
