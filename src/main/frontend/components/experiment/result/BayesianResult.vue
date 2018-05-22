<template>
  <v-container style="padding: 0px 10px 10px 10px">

    <device-selector
      @deviceChanged="deviceChanged"
      :selectedDevice="selectedDevice"
    ></device-selector>

    <p v-if="this.showHistogram() && this.histogramsToDate">
      Data calculated on
      <b>{{ this.histogramsToDate }}</b>.
    </p>

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

    data () {
      return {
        histogramsToDate: this.bayesianHistograms.metadata.toDate,
        histograms: this.bayesianHistograms.histograms,
        variantName: this.experiment.variants.find(v => v.name !== 'base').name
      }
    },

    methods: {
      getHistogramData (variantName) {
        let found = this.histograms && this.histograms.find(x => x.variantName === variantName)
        return found
      },

      getEqualizerData () {
        return this.bayesianEqualizer
      },

      updateVariantName ({ variantName }) {
        this.variantName = variantName
      },

      showHistogram () {
        return this.histograms && this.histograms.length > 0
      },

      showEqualizer () {
        return this.bayesianEqualizer && this.bayesianEqualizer.bars && this.bayesianEqualizer.bars.length > 0
      },

      deviceChanged ({device}) {
        this.$emit('deviceChangedOnBayesian', {
          device: device
        })
      }
    }
  }
</script>
