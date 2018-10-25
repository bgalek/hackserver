import { Record } from 'immutable'

const ExperimentGoalRecord = Record({
  hasHypothesis: false,
  leadingMetric: '',
  expectedDiffPercent: 0,
  testAlpha: 0,
  leadingMetricBaselineValue: 0,
  testPower: 0,
  requiredSampleSize: 0
})

export default class ExperimentGoal extends ExperimentGoalRecord {
}
