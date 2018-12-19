<template>
  <v-container style="padding: 0px 10px 10px 10px">

    <device-selector
      @deviceChanged="deviceChanged"
      :selectedDevice="selectedDevice"
    ></device-selector>

    <v-container v-for="bayes in shownBayes" :key="bayes.metricKey">

      <p class="mb-0 mt-2"><b>{{ bayes.metricLabel }}</b> histogram for <b>{{ bayes.metadata.deviceClass }}</b>
      calculated on <b>{{ bayes.metadata.toDate }}</b>
      </p>

      <v-layout row>
        <v-spacer></v-spacer>
        <variant-selector
          :experiment="experiment"
          :selectedVariantName="variantName"
          @variantNameChanged="updateVariantName"
          :showBase="false"
        ></variant-selector>
      </v-layout>

      <bayesian-histogram-chart :histogramData="bayes.histogram">
      </bayesian-histogram-chart>
    </v-container>

  </v-container>
</template>

<script>
  import DeviceSelector from './DeviceSelector'
  import BayesianHistogramChart from './BayesianHistogramChart'
  import VariantSelector from './VariantSelector'
  import {getMetricLabelByKey} from '../../../model/experiment/metrics'

  export default {
    props: ['experiment', 'bayesianHistograms', 'selectedDevice'],

    components: {
      BayesianHistogramChart,
      VariantSelector,
      DeviceSelector
    },

    data () {
      return {
        variantName: this.experiment.getFirstVariant() && this.experiment.getFirstVariant().name}
    },

    computed: {
      shownBayes: function () {
        return this.bayesianHistograms.metricStatistics &&
          this.bayesianHistograms.metricStatistics.map(it => {
            return {
              metricKey: it.metricName,
              metricLabel: getMetricLabelByKey(it.metricName),
              metadata: it.metadata,
              histogram: it.histograms.find(x => x.variantName === this.variantName)
            }
          })
      }
    },

    methods: {
      updateVariantName ({ variantName }) {
        this.variantName = variantName
      },

      deviceChanged ({device}) {
        this.$emit('deviceChangedOnBayesian', {
          device: device
        })
      }
    }
  }
</script>
