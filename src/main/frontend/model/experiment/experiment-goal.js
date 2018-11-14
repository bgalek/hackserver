import { Record } from 'immutable'

const ExperimentGoalRecord = Record({
  leadingMetric: '',
  expectedDiffPercent: 0,
  testAlpha: 0,
  leadingMetricBaselineValue: 0,
  testPower: 0,
  requiredSampleSize: 0,
  currentSampleSize: 0
})

export default class ExperimentGoal extends ExperimentGoalRecord {
  getProgressPercent () {
    if (!this.requiredSampleSize > 0 || !this.currentSampleSize > 0) {
      return 0
    }

    return Math.floor((100 * this.currentSampleSize / this.requiredSampleSize))
  }
}
