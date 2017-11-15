<template>
  <v-container><v-layout><v-flex offset-md1 md10 lg9 offset-xl2 xl8>
    <h1>Experiments</h1>

    <v-alert v-if="pivotError" color="error" icon="warning" value="true">
      Error while trying to get Pivot url: {{ pivotError }}
    </v-alert>

    <div id="experiments" v-if="experiments.length">
      <v-list two-line>
        <v-list-tile v-for="experiment in experiments" :key="experiment.id" @click="">
          <v-list-tile-content>
            <v-list-tile-title v-html="experiment.id"></v-list-tile-title>
            <v-list-tile-sub-title v-html="experiment.desc"></v-list-tile-sub-title>
          </v-list-tile-content>
          <v-list-tile-action>
            <v-badge>
              <v-chip small v-bind:color="variantColor(i)" v-for="(variant, i) in experiment.variants" :key="variant.name">
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
      <v-progress-circular v-if="pending" indeterminate v-bind:size="70" v-bind:width="7" color="purple"></v-progress-circular>
    </p>
  </v-flex></v-layout></v-container>
</template>

<script>
import { mapState, mapActions } from 'vuex'
import axios from 'axios'

export default {
  mounted () {
    this.getExperiments()
  },

  data () {
    return {
      pivotError: false
    }
  },

  computed: mapState({
    experiments: state => state.experiments,
    error: state => state.error.experiments,
    pending: state => state.pending.experiments
  }),

  methods: {
    ...mapActions(['getExperiments']),

    variantColor (i) {
      let colors = ['orange', 'cyan', 'yellow', 'green', 'pink', 'blue', 'amber', 'lime']
      if (i >= colors.length) {
        return 'grey'
      }
      return colors[i]
    },

    goToPivot (experimentId) {
      axios.post('http://pivot-nga-prod.allegrogroup.com/mkurl', {
        domain: 'http://pivot-nga-prod.allegrogroup.com',
        essence: {
          dataCube: '21b1',
          visualization: 'line-chart',
          filter: {
            clauses: [
              { dimension: 'experiment_id',
                values: {
                  setType: 'STRING',
                  elements: [ experimentId ]
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

