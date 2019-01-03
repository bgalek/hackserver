<template>
  <div>

    <template v-if="experiment.goal">
      <div>
        Improve the <span style="display:inline-block; border-bottom:1px solid black;">{{ leadingMetricLabel }}</span>
        by at least <span style="display:inline-block; border-bottom:1px solid black;">{{ experiment.goal.expectedDiffPercent}}</span>%
        <span v-if="leadingDevice" style="display:inline-block; border-bottom:1px solid black;">{{ leadingDevice }}</span>

        <template v-if="sampleSizeCalculatorEnabled">
          <br/>
          Baseline metric value:
        <span style="display:inline-block; border-bottom:1px solid black;" >
           {{ experiment.goal.leadingMetricBaselineValue}}%
        </span>
        </template>
      </div>

      <div v-if="sampleSizeCalculatorEnabled" class="mt-2">
        Required samples: <span style="display:inline-block; border-bottom:1px solid black;">{{ experiment.goal.requiredSampleSize}}</span> ,
        gathered:
        <v-progress-circular :rotate="270" color="blue" size="45" :value="experiment.goal.getProgressPercent()">
          <span class="black--text">{{ experiment.goal.getProgressPercent() + '%' }}</span>
        </v-progress-circular>
      </div>

    </template>

    <template v-if="!experiment.goal">
      No hypothesis
    </template>
  </div>
</template>

<script>
  import { getMetricByKey } from '../../model/experiment/metrics'

  export default {
    props: ['experiment'],

    data () {
      return {
      }
    },

    computed: {
      sampleSizeCalculatorEnabled () {
        return this.experiment.goal.testAlpha > 0
      },

      leadingMetricLabel () {
        return getMetricByKey(this.experiment.goal.leadingMetric).label
      },

      leadingDevice () {
        let device = this.experiment.getBaseDeviceClass()

        if (!device || device === 'all') {
          return ''
        } else {
          return 'on ' + device
        }
      }
    }
  }
</script>
