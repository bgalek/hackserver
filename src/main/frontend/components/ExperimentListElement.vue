<template>
  <v-list-tile @click="goToExperiment(experiment.id)">
    <v-list-tile-content>
      <v-list-tile-title v-html="experiment.id"></v-list-tile-title>
      <v-list-tile-sub-title v-html="experiment.desc"></v-list-tile-sub-title>
      <v-list-tile-sub-title v-html="experiment.activeFrom"></v-list-tile-sub-title>
    </v-list-tile-content>
    <v-list-tile-action>
      <v-badge>
        <v-chip small :color="variantColor(i, variant)" v-for="(variant, i) in experiment.variants" :key="variant.name"
                :disabled="true">
          {{ variant.name }}
        </v-chip>
        <v-tooltip left v-if="linkToData">
          <v-btn flat icon color="indigo" slot="activator" @click="goToPivot(experiment.id)">
            <v-icon>show_chart</v-icon>
          </v-btn>
          <span>Pivot Stats</span>
        </v-tooltip>
      </v-badge>
    </v-list-tile-action>
  </v-list-tile>
</template>

<script>
  import { variantColor } from '../utils/variantColor'
  import axios from 'axios'

  // TODO move to endpoint
  const PIVOT_PROD = 'http://pivot-nga-prod.allegrogroup.com'
  const PIVOT_TEST = 'http://pivot-nga-test.allegrogroup.com'

  const PIVOT = (window.location.hostname === 'chi.allegrogroup.com') ? PIVOT_PROD : PIVOT_TEST
  const CUBE = (window.location.hostname === 'chi.allegrogroup.com') ? '21b1' : 'ded9'

  export default {
    props: ['experiment', 'linkToData'],

    methods: {
      variantColor (i, variant) {
        if (this.isBase(variant)) {
          return variantColor(0)
        } else {
          return variantColor(i + 1)
        }
      },

      goToExperiment (experimentId) {
        return this.$router.push(`/experiments/${experimentId}`)
      },

      isBase (variant) {
        return variant.name === 'base'
      },

      goToPivot (experimentId) {
        axios.post(`${PIVOT}/mkurl`, {
          domain: `${PIVOT}`,
          essence: {
            dataCube: CUBE,
            visualization: 'line-chart',
            filter: {
              clauses: [
                {
                  dimension: 'experiment_id',
                  values: {
                    setType: 'STRING',
                    elements: [ experimentId ]
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
            ]
          }
        }).then(response => {
          this.pivotError = false
          window.open(response.data.url, '_blank')
        }).catch(error => {
          this.pivotError = error.message
        })
      }
    }
  }
</script>
