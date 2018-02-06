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

        const metricPivotNamesTest = {
          'tx_visit': '1000sum-06a',
          'p_value': 'sum_p_value',
          'gmv': 'sumtran-ed2'
        }

        const metricPivotNamesProd = {
          'tx_visit': 'sumvisi-159',
          'p_value': 'sum_p_value',
          'gmv': 'sumtran-7df'
        }

        return this.isProdEnv() ? metricPivotNamesProd[metric] : metricPivotNamesTest[metric]
      },

      pivotUrl () {
        const PIVOT_PROD = 'http://pivot.allegrogroup.com'
        const PIVOT_TEST = 'http://pivot-nga-test.allegrogroup.com'

        return this.isProdEnv() ? PIVOT_PROD : PIVOT_TEST
      },

      buildPivotRequest () {
        if (this.cubeType === 'metrics') {
          return {
            domain: this.pivotUrl(),
            essence: {
              dataCube: this.isProdEnv() ? '21b1' : 'ded9',
              visualization: 'line-chart',
              filter: {
                clauses: [
                  {
                    dimension: 'experiment_id',
                    values: {
                      setType: 'STRING',
                      elements: [ this.experimentId ]
                    }
                  },
                  {
                    dimension: '__time',
                    dynamic: {
                      op: 'timeRange',
                      operand: {
                        op: 'timeFloor',
                        operand: {
                          op: 'ref',
                          name: 'n'
                        },
                        duration: 'P1D'
                      },
                      duration: 'P1D',
                      'step': -7
                    }
                  }
                ]
              },
              splits: [
                { dimension: 'experiment_variant' },
                { dimension: '__time',
                  bucketAction: {
                    op: 'timeBucket',
                    duration: 'PT1H'
                  }
                }
              ],
              singleMeasure: this.pivotMetricCode()
            }
          }
        }

        if (this.cubeType === 'stats') {
          return {
            domain: this.pivotUrl(),
            essence: {
              dataCube: this.isProdEnv() ? 'b63e' : 'acd9',
              visualization: 'line-chart',
              filter: {
                clauses: [
                  {
                    dimension: 'experiment_id',
                    values: {
                      setType: 'STRING',
                      elements: [ this.experimentId ]
                    }
                  },
                  {
                    dimension: 'experiment_variant',
                    values: {
                      setType: 'STRING',
                      elements: [ this.variant ]
                    }
                  },
                  {
                    dimension: 'metric',
                    values: {
                      setType: 'STRING',
                      elements: [ this.metric ]
                    }
                  },
                  {
                    dimension: '__time',
                    dynamic: {
                      op: 'timeRange',
                      operand: {
                        op: 'timeFloor',
                        operand: {
                          op: 'ref',
                          name: 'n'
                        },
                        duration: 'P1D'
                      },
                      duration: 'P1D',
                      'step': -7
                    }
                  }
                ]
              },
              splits: [
                { dimension: 'device_class' },
                { dimension: '__time',
                  bucketAction: {
                    op: 'timeBucket',
                    duration: 'P1D'
                  }
                }
              ],
              singleMeasure: this.pivotMetricCode()
            }
          }
        }
      },

      goToPivot () {
        axios.post(`${this.pivotUrl()}/mkurl`, this.buildPivotRequest()).then(response => {
          window.open(response.data.url, '_blank')
        }).catch(error => {
          console.log('failed to generate Pivot link', error.message)
        })
      }
    }
  }
</script>
