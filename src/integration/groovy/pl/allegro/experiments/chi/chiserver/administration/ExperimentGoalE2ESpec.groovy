package pl.allegro.experiments.chi.chiserver.administration

import pl.allegro.experiments.chi.chiserver.BaseE2EIntegrationSpec

class ExperimentGoalE2ESpec extends BaseE2EIntegrationSpec {

    def "should create experiment with goal"(){
      given:
      def  goal = [
                leadingMetric: 'tx_visit',
                expectedDiffPercent: 2,
                leadingMetricBaselineValue : 5,
                testAlpha : 0.05,
                testPower : 0.80,
                requiredSampleSize: 1000
      ]

      when:
      def experiment = startedExperiment([goal: goal])

      then:
      with(experiment.goal) {
          println it
          assert it.leadingMetric == 'tx_visit'
          assert it.expectedDiffPercent == 2
          assert it.leadingMetricBaselineValue == 5
          assert it.testAlpha == 0.05
          assert it.testPower == 0.80
          assert it.requiredSampleSize == 1000
          assert it.currentSampleSize == 0
      }
    }
}
