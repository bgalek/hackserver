<template>
  <div>
    <template v-if="experiment.goal">
      <div>
        Improve the <span style="display:inline-block; border-bottom:1px solid black;">{{ leadingMetricLabel }}</span>
        by at least <span style="display:inline-block; border-bottom:1px solid black;">{{ experiment.goal.expectedDiffPercent}}</span>%
        <span v-if="leadingDevice" style="display:inline-block; border-bottom:1px solid black;">{{ leadingDevice }}</span>
      </div>

      <div v-if="sampleSizeCalculatorEnabled" class="mt-2">
        Required samples: <span style="display:inline-block; border-bottom:1px solid black;">{{ this.experiment.goal.requiredSampleSize}}</span> ,
        gathered: {{ leadingStatsBaseCount }} ({{ progressPercent }}%)
      </div>

    </template>

    <template v-if="!experiment.goal">
      No hypothesis
    </template>
  </div>
</template>

<script>
  import { getMetricByKey } from '../../model/experiment/metrics'
  import {mapState} from 'vuex'

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
      },

      leadingStatsBaseCount () {
        if (this.enrichedGoal && this.enrichedGoal.leadingStatsBaseCount > 0) {
          return this.enrichedGoal.leadingStatsBaseCount
        }
        return 0
      },

      progressPercent () {
        if (this.leadingStatsBaseCount && this.enrichedGoal.getProgressPercent()) {
          return this.enrichedGoal.getProgressPercent()
        }
        return 0
      },

      ...mapState({
        enrichedGoal: state => state.enrichedGoal.enrichedGoal
      })
    }
  }
</script>
