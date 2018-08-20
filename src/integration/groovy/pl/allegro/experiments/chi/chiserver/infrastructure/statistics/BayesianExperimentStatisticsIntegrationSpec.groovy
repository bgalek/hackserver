package pl.allegro.experiments.chi.chiserver.infrastructure.statistics

import org.springframework.beans.factory.annotation.Autowired
import pl.allegro.experiments.chi.chiserver.BaseE2EIntegrationSpec
import pl.allegro.experiments.chi.chiserver.domain.statistics.bayes.BayesianExperimentStatistics
import pl.allegro.experiments.chi.chiserver.domain.statistics.bayes.BayesianStatisticsRepository

import static pl.allegro.experiments.chi.chiserver.utils.SampleStatisticsRequests.sampleBayesianStatisticsRequest

class BayesianExperimentStatisticsIntegrationSpec extends BaseE2EIntegrationSpec {

    @Autowired
    BayesianStatisticsRepository bayesianStatisticsRepository

    def "should save and return bayesian statistics"() {
        given:
        def experiment = startedExperiment()

        and:
        def request = sampleBayesianStatisticsRequest([-0.2, -0.1, 0.1, 0.2], [100, 200, 250, 150], [
                experimentId: experiment.id,
                toDate      : '2018-04-01',
        ])

        when:
        def response = postBayesianStatistics(request)

        then:
        response.statusCode.value() == 200

        when:
        def result = fetchBayesianStatistics(experiment.id as String)

        then:
        result.device == 'all'
        result.experimentId == experiment.id
        result.toDate == '2018-04-01'
        result.variantBayesianStatistics.size() == 1

        and:
        def variant = result.variantBayesianStatistics[0]
        variant.variantName == 'variant-a'
        variant.samples.values == [-0.2, -0.1, 0.1, 0.2]
        variant.samples.counts == [100, 200, 250, 150]
        variant.outliersLeft == 10
        variant.outliersRight == 122
        variant.allCount() == 100 + 200 + 250 + 150 + 10 + 122
    }

    def "should evict old stats when new are coming"() {
        given:
        def experiment = startedExperiment()

        when:
        postBayesianStatistics(sampleBayesianStatisticsRequest([1], [1], [
                experimentId: experiment.id,
                variantName: 'variant-a',
                toDate      : '2018-01-01'
        ]))
        postBayesianStatistics(sampleBayesianStatisticsRequest([2], [2], [
                experimentId: experiment.id,
                variantName: 'variant-b',
                toDate      : '2018-01-01'
        ]))

        and:
        postBayesianStatistics(sampleBayesianStatisticsRequest([11], [16], [
                experimentId: experiment.id,
                variantName: 'variant-a',
                toDate      : '2018-01-01'
        ]))
        postBayesianStatistics(sampleBayesianStatisticsRequest([21], [26], [
                experimentId: experiment.id,
                variantName: 'variant-b',
                toDate      : '2018-01-01'
        ]))

        and:
        postBayesianStatistics(sampleBayesianStatisticsRequest([10], [15], [
                experimentId: experiment.id,
                variantName: 'variant-a',
                toDate      : '2018-01-02'
        ]))
        postBayesianStatistics(sampleBayesianStatisticsRequest([20], [25], [
                experimentId: experiment.id,
                variantName: 'variant-b',
                toDate      : '2018-01-02'
        ]))

        and:
        def result = fetchBayesianStatistics(experiment.id as String)

        then:
        result.device == 'all'
        result.experimentId == experiment.id
        result.toDate == '2018-01-02'
        result.variantBayesianStatistics.size() == 2

        def variantA = result.variantBayesianStatistics.find { it.variantName == 'variant-a' }
        def variantB = result.variantBayesianStatistics.find { it.variantName == 'variant-b' }

        variantA.samples.values == [10]
        variantA.samples.counts == [15]

        variantB.samples.values == [20]
        variantB.samples.counts == [25]
    }

    def "should update existing stats with new variants and return bayesian statistics"() {
        given:
        def experiment = startedExperiment()

        when:
        def response = postBayesianStatistics(sampleBayesianStatisticsRequest([-0.2, -0.1], [100, 200], [
                experimentId: experiment.id,
                variantName: 'variant-a'
        ]))

        then:
        response.statusCode.value() == 200

        when:
        response = postBayesianStatistics(sampleBayesianStatisticsRequest([-0.1, -0.2, 0.3, 0.6], [10, 200, 250, 1], [
                experimentId: experiment.id,
                variantName: 'variant-c'
        ]))

        then:
        response.statusCode.value() == 200

        when:
        def statistics = fetchBayesianStatistics(experiment.id as String)

        then:
        statistics.device == 'all'
        statistics.experimentId == experiment.id
        statistics.toDate == '2018-04-01'
        statistics.variantBayesianStatistics.size() == 2

        def variantA = statistics.variantBayesianStatistics.find { it.variantName == 'variant-a' }
        variantA.samples.values == [-0.2, -0.1]
        variantA.samples.counts == [100, 200]
        variantA.outliersLeft == 10
        variantA.outliersRight == 122
        variantA.allCount() == 100 + 200 + 10 + 122

        def variantC = statistics.variantBayesianStatistics.find { it.variantName == 'variant-c' }
        variantC.samples.values == [-0.1, -0.2, 0.3, 0.6]
        variantC.samples.counts == [10, 200, 250, 1]
        variantC.outliersLeft == 10
        variantC.outliersRight == 122
        variantC.allCount() == 593
    }

    BayesianExperimentStatistics fetchBayesianStatistics(String experimentId) {
        bayesianStatisticsRepository.experimentStatistics(experimentId as String, 'all').get()
    }
}
