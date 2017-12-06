<template>
  <v-container><v-layout><v-flex offset-md1 md10 lg9 offset-xl2 xl8>
    <h1>Experiments</h1>

    <v-alert v-if="pivotError" color="error" icon="warning" value="true">
      Error while trying to get Pivot url: {{ pivotError }}
    </v-alert>

    <div id="experiments" v-if="experiments.length">
      <v-list two-line>
        <v-list-tile v-for="experiment in experiments" :key="experiment.id" @click="goToExperiment(experiment.id)">
          <v-list-tile-content>
            <v-list-tile-title v-html="experiment.id"></v-list-tile-title>
            <v-list-tile-sub-title v-html="experiment.desc"></v-list-tile-sub-title>
            <v-list-tile-sub-title v-html="experiment.activeFrom"></v-list-tile-sub-title>
          </v-list-tile-content>
          <v-list-tile-action>
            <v-badge>
              <v-chip small :color="variantColor(i, variant)" v-for="(variant, i) in experiment.variants" :key="variant.name" :disabled="true">
              {{ variant.name }}
              </v-chip>

              <v-tooltip left>
                <v-btn flat icon color="indigo" slot="activator" @click="goToPivot(experiment.id)">
                    <v-icon>show_chart</v-icon>
                </v-btn>
                <span>Pivot Stats</span>
              </v-tooltip>
            </v-badge>
          </v-list-tile-action>
        </v-list-tile>
      </v-list>
    </div>

    <v-alert v-if="error" color="error" icon="warning" value="true">
      Couldn't load experiments: {{ error.message }}
    </v-alert>

    <p class="text-xs-center">
      <v-progress-circular v-if="pending" indeterminate :size="70" :width="7" color="purple"></v-progress-circular>
    </p>
  </v-flex></v-layout></v-container>
</template>

<script>
import { mapState, mapActions } from 'vuex'
import axios from 'axios'
import { variantColor } from '../utils/variantColor'

export default {
  mounted () {
    this.getExperiments()
  },

  data () {
    return {
      pivotError: false
    }
  },

  computed: {
    ...mapState({
      error: state => state.experiments.error.experiments,
      pending: state => state.experiments.pending.experiments
    }),
    experiments () {
      return this.sortExperiments(this.$store.state.experiments.experiments)
    }
  },

  methods: {
    ...mapActions(['getExperiments']),

    goToExperiment (experimentId) {
      this.$router.push(`/experiments/${experimentId}`)
    },

    goToPivot (experimentId) {
      axios.post('http://pivot-nga-prod.allegrogroup.com/mkurl', {
        domain: 'http://pivot-nga-prod.allegrogroup.com',
        essence: {
          dataCube: '21b1',
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
    },

    variantColor (i, variant) {
      if (this.isBase(variant)) {
        return variantColor(0)
      } else {
        return variantColor(i + 1)
      }
    },

    sortExperiments (experiments) {
      experiments.forEach(e => this.sortVariants(e.variants))

      const sortingKey = function (experiment) {
        return experiment.activeFrom ? experiment.activeFrom : '0' + experiment.id
      }

      experiments.sort((l, r) => sortingKey(r).localeCompare(sortingKey(l)))

      return experiments
    },

    sortVariants (variants) {
      variants.sort((l, r) => {
        if (this.isBase(l)) {
          return 1
        }
        if (this.isBase(r)) {
          return -1
        }
        return l.name.localeCompare(r.name)
      })
    },

    isBase (variant) {
      return variant.name === 'base'
    }
  }
}
</script>

