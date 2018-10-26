<template>
  <div>
    <template v-if="goal">
      <div>
        Improve the <span style="display:inline-block; border-bottom:1px solid black;">{{ leadingMetricLabel }}</span>
        by at least <span style="display:inline-block; border-bottom:1px solid black;">{{ goal.expectedDiffPercent}}</span>%
        <span v-if="leadingDevice" style="display:inline-block; border-bottom:1px solid black;">{{ leadingDevice }}</span>
      </div>

      <div v-if="sampleSizeCalculatorEnabled" class="mt-2">
        Required samples: <span style="display:inline-block; border-bottom:1px solid black;">{{ goal.requiredSampleSize}}</span> ,
        gathered: {{ gatheredSampleSize }} (1%)
      </div>

    </template>

    <template v-if="!goal">
      No hypothesis
    </template>
  </div>
</template>

<script>
  import { getMetricByKey } from '../../model/experiment/metrics'
  import {mapState} from 'vuex'


  export default {
    data () {
      return {
      }
    },

    computed: {
      sampleSizeCalculatorEnabled () {
        return this.goal.testAlpha > 0
      },

      leadingMetricLabel () {
        return getMetricByKey(this.goal.leadingMetric).label
      },

      gatheredSampleSize () {
        return  this.enrichedGoal.leadingStatsBase.count
      },

      leadingDevice () {
        let device = this.enrichedGoal.leadingDevice

        if (!device || device === 'all') {
          return ''
        } else {
          return 'on ' + device
        }
      },

      ...mapState({
        enrichedGoal: state => state.enrichedGoal.enrichedGoal,
        goal: state => state.enrichedGoal.enrichedGoal.goal
      })
    }
  }
</script>
