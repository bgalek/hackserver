<template>
  <v-container>
    <v-layout>
      <v-flex offset-md1 md10 lg9 offset-xl2 xl8>
        <h1>Experiment: {{ $route.params.experimentId }}</h1>

        <h2>Variants</h2>

        <v-btn
          v-for="(variant, i) in experiment.variants"
          :color="variantColor(i)"
          :key="variant.name"
          @click="goToCookieBaker(experiment.id, variant.name)"
          class="white--text"
        >
          {{ variant.name }}
          <v-icon right>add</v-icon>
        </v-btn>

        <v-alert v-if="error" color="error" icon="warning" value="true">
          Couldn't load experiment {{ $route.params.experimentId }} : {{ error.message }}
        </v-alert>

        <p class="text-xs-center">
          <v-progress-circular v-if="pending" indeterminate :size="70" :width="7" color="purple"></v-progress-circular>
        </p>

        <h2>Devices</h2>
        <v-radio-group v-model="device" row>
          <v-radio id="all" label="All" value="all"></v-radio>
          <v-radio id="desktop" label="Desktop" value="desktop"></v-radio>
          <v-radio id="smartphone" label="Smartphone" value="smartphone"></v-radio>
          <v-radio id="tablet" label="Tablet" value="tablet"></v-radio>
        </v-radio-group>

        <h2>Metrics</h2>
        <div v-if="experimentStatistics.metrics">
          <h3>State from {{experimentStatistics.toDate}}({{experimentStatistics.durationDays}} days)</h3>
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

      </v-flex>
    </v-layout>
  </v-container>
</template>

<script>
  import {mapState, mapActions} from 'vuex'
  import {variantColor} from '../utils/variantColor'
  import {cookieBakerHost} from '../utils/cookieBakerHost'
  import VDivider from "vuetify/src/components/VDivider/VDivider";

  export default {
    data() {
      return {
        headers: [
          {text: 'Variant'},
          {text: 'Value'},
          {text: 'Diff'},
          {text: 'P Value'},
          {text: 'Count'}
        ],
        metricNames: {
          'txPerVisit': 'Transaction Per Visit',
          'gmv': 'GMV'
        },
        device: 'all'
      }
    },

    components: {VDivider},
    mounted() {
      this.getExperiment({params: {experimentId: this.$route.params.experimentId}})
      this.mountExperimentStatistics()

    },

    computed: mapState({
      experiment: state => state.experiment.experiment,
      error: state => state.experiment.error.experiment,
      pending: state => state.experiment.pending.experiment,
      experimentStatistics: state => {
        let stats = state.experimentStatistics.experimentStatistics
        let mappedMetrics = {}
        for (let metric_name in stats.metrics) {
          if (stats.metrics.hasOwnProperty(metric_name)) {
            let mappedVariants = []
            for (let variant_name in stats.metrics[metric_name]) {
              if (stats.metrics[metric_name].hasOwnProperty(variant_name)) {
                let variant = stats.metrics[metric_name][variant_name]
                if (variant_name !== 'base') {
                  mappedVariants.push({
                    variant: variant_name,
                    value: variant.value,
                    diff: variant.diff,
                    count: variant.count,
                    pValue: variant.pValue
                  })
                } else {
                  mappedVariants.unshift({
                    variant: variant_name,
                    value: variant.value,
                    diff: variant.diff,
                    count: variant.count,
                    pValue: variant.pValue
                  })
                }

              }
            }
            mappedMetrics[metric_name] = mappedVariants
          }
        }
        return {
          id: stats.id,
          durationDays: stats.duration / (3600 * 24 * 1000) ,
          device: stats.device,
          toDate: stats.toDate,
          metrics: mappedMetrics
        }
      },
      experimentStatisticsError: state => state.experimentStatistics.error.experimentStatistics,
      experimentStatisticsPending: state => state.experimentStatistics.pending.experimentStatistics
    }),

    methods: {
      ...mapActions(['getExperiment', 'getExperimentStatistics']),

      variantColor(i) {
        return variantColor(i)
      },

      goToCookieBaker(experimentId, variantName) {
        let protocol = 'https://'
        let host = cookieBakerHost()
        let url = protocol + host + `/chi/cookie-baker.html?chi=${experimentId}!${variantName}&redirect=${protocol + host + '/'}`
        window.open(url, '_blank')
      },

      filterByDevice(device) {
        this.$router.push({
          name: 'experiment',
          params: {experimentId: this.$route.params.experimentId},
          query: {device: this.device}
        })
        this.mountExperimentStatistics()
      },

      mountExperimentStatistics() {
        if (this.$route.query.device) {
          this.getExperimentStatistics({
            params: {
              experimentId: this.$route.params.experimentId,
              device: this.$route.query.device
            }
          })
        } else {
          this.getExperimentStatistics({
            params: {
              experimentId: this.$route.params.experimentId
            }
          })
        }
      }
    },

    watch: {
      device: function(val) { this.filterByDevice(val)}
    }
  }
</script>
