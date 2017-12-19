<template>
      <v-btn icon @click="goToPivot()">
        <v-icon>fa-line-chart</v-icon>
      </v-btn>
</template>
<script>
  import axios from 'axios'

  export default {
    props: {
      selectedMetricName: String,
      cubeType: String,
      experimentId: String
    },

    methods: {
      isProdEnv () {
        return window.location.hostname === 'chi.allegrogroup.com'
      },

      cubeId () {
        if (this.cubeType === 'metrics') {
          return this.isProdEnv() ? '21b1' : 'ded9'
        }
      },

      pivotUrl () {
        const PIVOT_PROD = 'http://pivot.allegrogroup.com'
        const PIVOT_TEST = 'http://pivot-nga-test.allegrogroup.com'

        return this.isProdEnv() ? PIVOT_PROD : PIVOT_TEST
      },

      goToPivot () {
        axios.post(`${this.pivotUrl()}/mkurl`, {
          domain: this.pivotUrl(),
          essence: {
            dataCube: this.cubeId(),
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
            singleMeasure: this.selectedMetricName
          }
        }).then(response => {
          window.open(response.data.url, '_blank')
        }).catch(error => {
          console.log('failed to generate Pivot link', error.message)
        })
      }
    }
  }
</script>
