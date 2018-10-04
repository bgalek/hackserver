package pl.allegro.experiments.chi.chiserver.infrastructure.statistics

import org.springframework.web.client.HttpClientErrorException
import pl.allegro.experiments.chi.chiserver.BaseE2EIntegrationSpec
import spock.lang.Unroll

import static pl.allegro.experiments.chi.chiserver.utils.SampleStatisticsRequests.sampleClassicStatisticsRequest

class ClassicExperimentStatisticsIntegrationSpec extends BaseE2EIntegrationSpec {

    def "should save and return classic statistics"() {
        given:
        def experiment = startedExperiment()
        def request = sampleClassicStatisticsRequest([
                experimentId: experiment.id,
                metricName  : "tx_daily"
        ])

        when:
        def response = postClassicStatistics(request)

        then:
        response.statusCode.value() == 200

        when:
        request = sampleClassicStatisticsRequest([
                experimentId: experiment.id,
                data        : [
                        value : 1.23,
                        diff  : 0.0003,
                        pValue: 0.3,
                        count : 1684500
                ]
        ])

        and:
        response = postClassicStatistics(request)

        then:
        response.statusCode.value() == 200

        when:
        def statistics = fetchStatistics(experiment.id as String)

        then:
        statistics.device == "all"
        statistics.experimentId == experiment.id
        statistics.toDate == '2018-06-01'
        statistics.metrics.size() == 2

        and:
        def txDaily = statistics.metrics.tx_daily.someVariant
        txDaily.value == 0.07
        txDaily.diff == 0.0003
        txDaily.pValue == 0.3
        txDaily.count == 1684500

        and:
        def gmv = statistics.metrics.gmv.someVariant
        gmv.value == 1.23
        gmv.diff == 0.0003
        gmv.pValue == 0.3
        gmv.count == 1684500
    }

    def "should not allow saving statistics if Chi-Token not passed"() {
        given:
        def experiment = startedExperiment()
        def request = sampleClassicStatisticsRequest([
                experimentId: experiment.id,
                metricName  : "tx_daily"
        ])

        when:
        post("/api/statistics/", request)

        then:
        thrown(HttpClientErrorException)
    }

    @Unroll
    def "should return nothing on #description"() {
        given:
        def experiment = startedExperiment()
        def request = sampleClassicStatisticsRequest([
                experimentId  : experiment.id,
                variantName   : "variant-a",
                toDate        : '2018-06-01',
                durationMillis: 691200000L
        ])

        when:
        def response = postClassicStatistics(request)

        then:
        response.statusCode.value() == 200

        when:
        request = sampleClassicStatisticsRequest([
                experimentId  : experiment.id,
                variantName   : "variant-b",
                toDate        : toDate,
                durationMillis: durationMillis
        ])

        and:
        response = postClassicStatistics(request)

        then:
        response.statusCode.value() == 200

        when:
        def result = fetchStatistics(experiment.id as String)

        then:
        result == null

        where:
        description         | toDate       | durationMillis
        'date mismatch'     | '2018-06-02' | 691200000L
        'duration mismatch' | '2018-06-01' | 961200000L
    }

    def "should return nothing variant mismatch"() {
        given:
        def experiment = startedExperiment()
        def request = sampleClassicStatisticsRequest([
                expId      : experiment.id,
                metricName : "tx_daily",
                variantName: "variant-a"
        ])

        when:
        def response = postClassicStatistics(request)

        then:
        response.statusCode.value() == 200

        when:
        request = sampleClassicStatisticsRequest([
                expId      : experiment.id,
                metricName : "gmv",
                variantName: "variant-b"
        ])

        and:
        response = postClassicStatistics(request)

        then:
        response.statusCode.value() == 200

        when:
        def result = fetchStatistics(experiment.id as String)

        then:
        result == null
    }

    def "should evict old stats when new are coming"() {
        given:
        def experiment = startedExperiment()

        when:
        postClassicStatistics(sampleClassicStatisticsRequest(
                [experimentId: experiment.id, variantName: 'variant-a', toDate: '2018-01-01', data : [value : 1.11, diff  : 2.011]]))
        postClassicStatistics(sampleClassicStatisticsRequest(
                [experimentId: experiment.id, variantName: 'variant-b', toDate: '2018-01-01', data : [value : 1.11, diff  : 2.011]]))
        postClassicStatistics(sampleClassicStatisticsRequest(
                [experimentId: experiment.id, variantName: 'variant-a', toDate: '2018-01-01', data : [value : 1.11, diff  : 0.011]]))
        postClassicStatistics(sampleClassicStatisticsRequest(
                [experimentId: experiment.id, variantName: 'variant-b', toDate: '2018-01-01', data : [value : 0.12, diff  : 0.012]]))
        postClassicStatistics(sampleClassicStatisticsRequest(
                [experimentId: experiment.id, variantName: 'variant-a', toDate: '2018-01-02', data : [value : 0.13, diff  : 0.013]]))
        postClassicStatistics(sampleClassicStatisticsRequest(
                [experimentId: experiment.id, variantName: 'variant-b', toDate: '2018-01-02', data : [value : 0.14, diff  : 0.014]]))

        and:
        def statistics = fetchStatistics(experiment.id as String)

        then:
        statistics.device == "all"
        statistics.experimentId == experiment.id
        statistics.toDate == '2018-01-02'
        statistics.metrics.size() == 1
        statistics.metrics.gmv.size() == 2

        and:
        def variantA = statistics.metrics.gmv.'variant-a'
        variantA.value == 0.13
        variantA.diff == 0.013

        and:
        def variantB = statistics.metrics.gmv.'variant-b'
        variantB.value == 0.14
        variantB.diff == 0.014
    }

    def "should update existing stats with new variants and return classic statistics"() {
        given:
        def experiment = startedExperiment()

        and:
        def request = sampleClassicStatisticsRequest([
                experimentId: experiment.id,
                variantName: 'variant-a',
                toDate: '2018-06-01',
                data: [
                        value: 0.1,
                ]
        ])

        when:
        def response = postClassicStatistics(request)

        then:
        response.statusCode.value() == 200

        when:
        request = sampleClassicStatisticsRequest([
                experimentId: experiment.id,
                variantName: 'variant-b',
                toDate: '2018-06-01',
                data: [
                        value: 0.2,
                ]
        ])

        and:
        response = postClassicStatistics(request)

        then:
        response.statusCode.value() == 200

        when:
        def statistics = fetchStatistics(experiment.id as String)

        then:
        statistics.device == "all"
        statistics.experimentId == experiment.id
        statistics.toDate == '2018-06-01'
        statistics.metrics.size() == 1
        statistics.metrics.gmv.size() == 2

        def variantA = statistics.metrics.gmv.'variant-a'
        variantA.value == 0.1

        def variantB = statistics.metrics.gmv.'variant-b'
        variantB.value == 0.2
    }
}
