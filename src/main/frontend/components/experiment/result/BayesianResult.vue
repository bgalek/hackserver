<template>
  <v-container>

    <h4>Visits conversion equalizer</h4>
    <bayesian-equalizer-chart v-if="this.showEqualizer()"
      :equalizerData="this.getEqualizerData()"
    ></bayesian-equalizer-chart>

    <h3>Results based on bayesian analysis</h3>
    <br/>
    <h4>Visits conversion histogram</h4>
    <v-layout row v-if="this.show()">
      <v-spacer></v-spacer>
      <variant-selector
        :experiment="experiment"
        :selectedVariantName="this.variantName"
        @variantNameChanged="updateVariantName"
        :showBase="false"
      ></variant-selector>
    </v-layout>
    <bayesian-histogram-chart v-if="this.showHistogram()"
      :histogramData="this.getHistogramData(this.variantName)"
    >
    </bayesian-histogram-chart>
  </v-container>
</template>

<script>
  import BayesianHistogramChart from './BayesianHistogramChart'
  import BayesianEqualizerChart from './BayesianEqualizerChart'
  import VariantSelector from './VariantSelector'

  export default {
    props: ['experiment', 'bayesianStatistics', 'bayesianEqualizer'],

    components: {
      BayesianHistogramChart,
      BayesianEqualizerChart,
      VariantSelector
    },

    data () {
      return {
        variantName: this.experiment.variants.find(v => v.name !== 'base').name
      }
    },

    methods: {
      getHistogramData (variantName) {
        let found = this.bayesianStatistics && this.bayesianStatistics.find(x => x.variantName === variantName)
        return found && found.samples
      },

      getEqualizerData () {
        return this.bayesianEqualizer
      },

      updateVariantName ({ variantName }) {
        this.variantName = variantName
      },

      show () {
        return this.bayesianStatistics && this.bayesianStatistics.length > 0
      },

      showHistogram () {
        return this.bayesianStatistics && this.bayesianStatistics.length > 0
      },

      showEqualizer () {
        console.log('this.bayesianEqualizer', this.bayesianEqualizer)
        return this.bayesianEqualizer && this.bayesianEqualizer.bars && this.bayesianEqualizer.bars.length > 0
      }
    }
  }
</script>
