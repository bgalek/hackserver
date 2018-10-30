import { Record } from 'immutable'

const EnrichedGoalRecord = Record({
  requiredSampleSize: 0,
  leadingStatsBaseCount: 0
})

export default class EnrichedGoal extends EnrichedGoalRecord {
  getProgressPercent () {
    const currentSampleSize = this.leadingStatsBaseCount
    const requiredSampleSize = this.requiredSampleSize

    if (!requiredSampleSize > 0 || !currentSampleSize > 0) {
      return 0
    }

    return (100 * currentSampleSize / requiredSampleSize).toFixed(1)
  }
}
