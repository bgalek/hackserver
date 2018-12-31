package pl.allegro.experiments.chi.chiserver.administration

import org.springframework.web.client.HttpClientErrorException
import pl.allegro.experiments.chi.chiserver.BaseE2EIntegrationSpec
import spock.lang.Unroll

import static org.springframework.http.HttpStatus.BAD_REQUEST

class ExperimentGoalE2ESpec extends BaseE2EIntegrationSpec {

    def '''should create experiment with goal.hypothesis and with goal.testConfiguration
           for non-binary metrics'''(){
      given:
      def  goal = [
                leadingMetric: 'tx_visit',
                expectedDiffPercent: 2,
                leadingMetricBaselineValue : 5,
                testAlpha : 0.01,
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
          assert it.testAlpha == 0.01
          assert it.testPower == 0.80
          assert it.requiredSampleSize == 1000
          assert it.currentSampleSize == 0
      }
    }

    @Unroll
    def '''should not allow to create incomplete testConfiguration when
           testAlpha: #testAlpha,
           testPower: #testPower
           ''' (){
        given:
        def  goal = [
                leadingMetric: 'tx_visit',
                expectedDiffPercent: 2,
                leadingMetricBaselineValue : 5,
                testAlpha : testAlpha,
                testPower : testPower,
                requiredSampleSize: 1000
        ]

        when:
        startedExperiment([goal: goal])

        then:
        def exception = thrown HttpClientErrorException
        exception.statusCode == BAD_REQUEST

        where:
        testAlpha | testPower
        0         | 0
        null      | null
        0         | 0.80
        0.05      | 0
        null      | 0.80
        0.05      | null
        0.009     | 0.80
        0.01      | 0.79
    }

    def '''should create experiment with goal.hypothesis but without goal.testConfiguration
           for non-binary metrics''' (){
        given:
        def  goal = [
                leadingMetric: 'gmv',
                expectedDiffPercent: 2,
        ]

        when:
        def experiment = startedExperiment([goal: goal])

        then:
        with(experiment.goal) {
            println it
            assert it.leadingMetric == 'gmv'
            assert it.expectedDiffPercent == 2
            assert it.leadingMetricBaselineValue == null
            assert it.testAlpha == null
            assert it.testPowe == null
            assert it.requiredSampleSize == null
            assert it.currentSampleSize == null
        }
    }
}
