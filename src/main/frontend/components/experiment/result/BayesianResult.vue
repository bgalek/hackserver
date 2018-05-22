<template>
  <v-container style="padding: 0px 10px 10px 10px">

    {{ this.selectedDevice }}
    <device-selector
      @deviceChanged="deviceChanged"
      :selectedDevice="selectedDevice"
      where="Bayes"
    ></device-selector>

    <v-container v-if="this.showEqualizer()">
      <h4>Visits conversion equalizer</h4>
      <br/>
      <bayesian-equalizer-chart v-if="this.showEqualizer()"
                                :equalizerData="this.getEqualizerData()"

      ></bayesian-equalizer-chart>
      <v-spacer></v-spacer>
    <br/>
    </v-container>

    <v-container v-if="this.showHistogram()">
      <h4>Visits conversion histogram</h4>
      <v-layout row>
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
  </v-container>
</template>

<script>
  import DeviceSelector from './DeviceSelector'
  import BayesianHistogramChart from './BayesianHistogramChart'
  import BayesianEqualizerChart from './BayesianEqualizerChart'
  import VariantSelector from './VariantSelector'

  export default {
    props: ['experiment', 'bayesianHistograms', 'bayesianEqualizer', 'selectedDevice'],

    components: {
      BayesianHistogramChart,
      BayesianEqualizerChart,
      VariantSelector,
      DeviceSelector
    },

    updated () {
      console.log('- BayesianResult: i am updated to ', this.selectedDevice)
    },

    data () {
      return {
        variantName: this.experiment.variants.find(v => v.name !== 'base').name
      }
    },

    methods: {
      getHistogramData (variantName) {
        let found = this.bayesianHistograms && this.bayesianHistograms.find(x => x.variantName === variantName)
        return found
      },

      getEqualizerData () {
        return this.bayesianEqualizer
      },

      updateVariantName ({ variantName }) {
        this.variantName = variantName
      },

      showHistogram () {
        return this.bayesianHistograms && this.bayesianHistograms.length > 0
      },

      showEqualizer () {
        return this.bayesianEqualizer && this.bayesianEqualizer.bars && this.bayesianEqualizer.bars.length > 0
      },

      deviceChanged ({device}) {
        console.log('deviceChangedOnBayesian')
        this.$emit('deviceChangedOnBayesian', {
          device: device,
          where: 'Bayesian'
        })
      }
    }
  }
</script>
