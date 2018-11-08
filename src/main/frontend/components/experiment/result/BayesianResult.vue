<template>
  <v-container style="padding: 0px 10px 10px 10px">

    <device-selector
      @deviceChanged="deviceChanged"
      :selectedDevice="selectedDevice"
    ></device-selector>

    <p v-if="this.histogramData && this.histogramsToDate">
      Data calculated on
      <b>{{ this.histogramsToDate }}</b>.
    </p>

    <v-container v-if="this.showEqualizer()">
      <h4>Visits conversion equalizer</h4>
      <br/>
      <bayesian-equalizer-chart v-if="this.showEqualizer()"
                                :equalizerData="bayesianEqualizer"

      ></bayesian-equalizer-chart>
      <v-spacer></v-spacer>
    <br/>
    </v-container>

    <v-container v-if="this.histogramData">
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
      <bayesian-histogram-chart v-if="this.histogramData"
        :histogramData="histogramData"
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
        histogramsToDate: this.bayesianHistograms.metadata && this.bayesianHistograms.metadata.toDate,
        variantName: this.experiment.getFirstVariant() && this.experiment.getFirstVariant().name
      }
    },

    computed: {
      histogramData: function () {
        const histograms = this.bayesianHistograms.histograms

        return histograms && histograms.find(x => x.variantName === this.variantName)
      }
    },

    methods: {
      updateVariantName ({ variantName }) {
        this.variantName = variantName
      },

      showHistogram () {
        return this.histogramData
      },

      showEqualizer () {
        return this.experiment.isMultiVariant() &&
               this.bayesianEqualizer &&
               this.bayesianEqualizer.bars && this.bayesianEqualizer.bars.length > 0
      },

      deviceChanged ({device}) {
        this.$emit('deviceChangedOnBayesian', {
          device: device
        })
      }
    }
  }
</script>
