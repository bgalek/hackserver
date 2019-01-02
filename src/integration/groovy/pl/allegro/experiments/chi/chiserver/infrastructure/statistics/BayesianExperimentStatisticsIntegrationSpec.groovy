package pl.allegro.experiments.chi.chiserver.infrastructure.statistics

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.client.HttpClientErrorException
import pl.allegro.experiments.chi.chiserver.BaseE2EIntegrationSpec
import pl.allegro.experiments.chi.chiserver.domain.statistics.bayes.BayesianExperimentStatistics
import pl.allegro.experiments.chi.chiserver.domain.statistics.bayes.BayesianExperimentStatisticsForVariant
import pl.allegro.experiments.chi.chiserver.domain.statistics.bayes.BayesianStatisticsForVariantRepository
import pl.allegro.experiments.chi.chiserver.domain.statistics.bayes.BayesianStatisticsRepository

import static pl.allegro.experiments.chi.chiserver.utils.SampleStatisticsRequests.sampleBayesianStatisticsRequest

class BayesianExperimentStatisticsIntegrationSpec extends BaseE2EIntegrationSpec {

    @Autowired
    BayesianStatisticsRepository bayesianStatisticsRepository

    @Autowired
    BayesianStatisticsForVariantRepository bayesianStatisticsForVariantRepository

    def "should save and return bayesian statistics, old stats should be removed"() {
        given:
        def experiment = startedExperiment()

        when:
        def responseVisit1 = postBayesianStatistics(sampleBayesianStatisticsRequest([-0.2, -0.1, 0.1, 0.2], [100, 200, 250, 150], [
                experimentId: experiment.id,
                toDate      : '2018-03-15',
                metricName  : 'tx_visit'
        ]))
        def responseVisit2 = postBayesianStatistics(sampleBayesianStatisticsRequest([-0.5, -0.1, 0.1, 0.5], [10, 20, 25, 15], [
                experimentId: experiment.id,
                toDate      : '2018-03-01',
                metricName  : 'tx_visit'
        ]))
        def responseCmuid = postBayesianStatistics(sampleBayesianStatisticsRequest([-0.3, -0.1, 0.1, 0.3], [1, 2, 2, 1], [
                experimentId: experiment.id,
                toDate      : '2018-04-01',
                metricName  : 'tx_cmuid'
        ]))

        then:
        responseVisit1.statusCode.value() == 200
        responseVisit2.statusCode.value() == 200
        responseCmuid.statusCode.value() == 200

        when:
        def results = fetchBayesianStatistics(experiment.id as String)

        then:
        results.size == 2

        def resultVisit = results.find {it.metricName == 'tx_visit'}
        resultVisit.device == 'all'
        resultVisit.experimentId == experiment.id
        resultVisit.toDate == '2018-03-15'
        resultVisit.variantBayesianStatistics.size() == 1

        def variant = resultVisit.variantBayesianStatistics[0]
        variant.variantName == 'variant-a'
        variant.samples.values == [-0.2, -0.1, 0.1, 0.2]
        variant.samples.counts == [100, 200, 250, 150]
        variant.outliersLeft == 10
        variant.outliersRight == 122
        variant.allCount() == 100 + 200 + 250 + 150 + 10 + 122

        def resultCmuid = results.find {it.metricName == 'tx_cmuid'}
        resultCmuid.device == 'all'
        resultCmuid.experimentId == experiment.id
        resultCmuid.toDate == '2018-04-01'
        resultCmuid.variantBayesianStatistics.size() == 1
    }

    def "should save and return bayesian statistics for multiple devices"() {
        given:
        def experiment = startedExperiment()

        and:
        def requestSmartphone = sampleBayesianStatisticsRequest([-0.1, 0, 0.1, 0.2], [101, 201, 251, 151], [
                experimentId: experiment.id,
                toDate      : '2018-04-01',
                device      : 'smartphone',
                metricName  : 'tx_visit'
        ])

        and:
        def requestAll = sampleBayesianStatisticsRequest([-0.2, -0.1, 0.1, 0.2], [100, 200, 250, 150], [
                experimentId: experiment.id,
                toDate      : '2018-04-01',
                device      : 'all',
                metricName  : 'tx_visit'
        ])

        when:
        def responseSmartphone = postBayesianStatistics(requestSmartphone)
        def responseAll = postBayesianStatistics(requestAll)

        then:
        responseSmartphone.statusCode.value() == 200
        responseAll.statusCode.value() == 200

        when:
        def result = fetchBayesianStatistics(experiment.id as String)
        def resultSmartphone = result.find {it -> it.device == 'phone'}
        def resultAll = result.find {it -> it.device == 'all'}

        then:
        def variantSmartphone = resultSmartphone.variantBayesianStatistics[0]
        variantSmartphone.variantName == 'variant-a'
        variantSmartphone.samples.values == [-0.2, -0.1, 0.1, 0.2]
        variantSmartphone.samples.counts == [100, 200, 250, 150]
        variantSmartphone.outliersLeft == 10
        variantSmartphone.outliersRight == 122
        variantSmartphone.allCount() == 100 + 200 + 250 + 150 + 10 + 122

        and:
        def variantAll = resultAll.variantBayesianStatistics[0]
        variantAll.variantName == 'variant-a'
        variantAll.samples.values == [-0.2, -0.1, 0.1, 0.2]
        variantAll.samples.counts == [100, 200, 250, 150]
        variantAll.outliersLeft == 10
        variantAll.outliersRight == 122
        variantAll.allCount() == 100 + 200 + 250 + 150 + 10 + 122

    }

    def "should not allow saving bayes statistics if Chi-Token not passed"() {
        given:
        def experiment = startedExperiment()
        def request = sampleBayesianStatisticsRequest([-0.2, -0.1, 0.1, 0.2], [100, 200, 250, 150], [
                experimentId: experiment.id,
                toDate      : '2018-04-01',
                metricName  : 'tx_visit'
        ])

        when:
        post("/api/bayes/statistics/", request)

        then:
        thrown(HttpClientErrorException)
    }

    def "should evict old stats when new are coming"() {
        given:
        def experiment = startedExperiment()

        when:
        postBayesianStatistics(sampleBayesianStatisticsRequest([1], [1], [
                experimentId: experiment.id,
                variantName : 'variant-a',
                toDate      : '2018-01-01',
                metricName  : 'tx_visit'
        ]))
        postBayesianStatistics(sampleBayesianStatisticsRequest([2], [2], [
                experimentId: experiment.id,
                variantName : 'variant-b',
                toDate      : '2018-01-01',
                metricName  : 'tx_visit'
        ]))

        and:
        postBayesianStatistics(sampleBayesianStatisticsRequest([11], [16], [
                experimentId: experiment.id,
                variantName : 'variant-a',
                toDate      : '2018-01-01',
                metricName  : 'tx_visit'
        ]))
        postBayesianStatistics(sampleBayesianStatisticsRequest([21], [26], [
                experimentId: experiment.id,
                variantName : 'variant-b',
                toDate      : '2018-01-01',
                metricName  : 'tx_visit'
        ]))

        and:
        postBayesianStatistics(sampleBayesianStatisticsRequest([10], [15], [
                experimentId: experiment.id,
                variantName : 'variant-a',
                toDate      : '2018-01-02',
                metricName  : 'tx_visit'
        ]))
        postBayesianStatistics(sampleBayesianStatisticsRequest([20], [25], [
                experimentId: experiment.id,
                variantName : 'variant-b',
                toDate      : '2018-01-02',
                metricName  : 'tx_visit'
        ]))

        and:
        def result = fetchBayesianStatistics(experiment.id as String)[0]

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

        mongoTemplate.findAll(BayesianExperimentStatisticsForVariant, "bayesianExperimentStatistics").size() == 2
    }

    def "should update existing stats with new variants and return bayesian statistics"() {
        given:
        def experiment = startedExperiment()

        when:
        def response = postBayesianStatistics(sampleBayesianStatisticsRequest([-0.2, -0.1], [100, 200], [
                experimentId: experiment.id,
                variantName : 'variant-a',
                metricName  : 'tx_visit'
        ]))

        then:
        response.statusCode.value() == 200

        when:
        response = postBayesianStatistics(sampleBayesianStatisticsRequest([-0.1, -0.2, 0.3, 0.6], [10, 200, 250, 1], [
                experimentId: experiment.id,
                variantName : 'variant-c',
                metricName  : 'tx_visit'
        ]))

        then:
        response.statusCode.value() == 200

        when:
        def statistics = fetchBayesianStatistics(experiment.id as String)[0]

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

    def "should count number of experiments with bayesian statistics"(){
        given:
        5.times {
            postBayesianStatistics(sampleBayesianStatisticsRequest([1], [1], [
                    experimentId: ""+it,
                    variantName : 'base',
                    toDate      : '2018-01-01',
                    metricName  : 'tx_visit'
            ]))
            postBayesianStatistics(sampleBayesianStatisticsRequest([1], [1], [
                    experimentId: ""+it,
                    variantName : 'variant-a',
                    toDate      : '2018-01-01',
                    metricName  : 'tx_visit'
            ]))
        }
        postBayesianStatistics(sampleBayesianStatisticsRequest([1], [1], [
                experimentId: "the old one",
                variantName : 'base',
                toDate      : '2017-01-01',
                metricName  : 'tx_visit'
        ]))

        expect:
        bayesianStatisticsForVariantRepository.countNumberExperimentsWithStats() == 5
    }

    List<BayesianExperimentStatistics> fetchBayesianStatistics(String experimentId) {
        bayesianStatisticsRepository.experimentStatistics(experimentId as String)
    }
}
