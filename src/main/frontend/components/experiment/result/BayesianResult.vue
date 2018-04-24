<template>
  <v-container v-if="this.show()">
    <h3>Results based on bayesian analysis</h3>

    <v-layout row>
      <v-spacer></v-spacer>
      <variant-selector
        :experiment="experiment"
        :selectedVariantName="this.variantName"
        @variantNameChanged="updateVariantName"
      ></variant-selector>
    </v-layout>
    <bayesian-histogram-chart
      :histogramData="this.getHistogramData(this.variantName)"
    >
    </bayesian-histogram-chart>
  </v-container>
</template>

<script>
  import BayesianHistogramChart from './BayesianHistogramChart'
  import VariantSelector from './VariantSelector'

  export default {
    props: ['experiment', 'bayesianStatistics'],

    components: {
      BayesianHistogramChart,
      VariantSelector
    },

    data () {
      return {
        variantName: ''
      }
    },

    methods: {
      getHistogramData (variantName) {
        let found = this.bayesianStatistics && this.bayesianStatistics.find(x => x.variantName === variantName)
        return found && found.samples
      },

      updateVariantName ({ variantName }) {
        this.variantName = variantName
      },

      show () {
        return this.bayesianStatistics && this.bayesianStatistics.length > 0
      }
    }
  }
</script>
