package pl.allegro.experiments.chi.chiserver.infrastructure.statistics

import org.springframework.web.client.HttpClientErrorException
import pl.allegro.experiments.chi.chiserver.BaseE2EIntegrationSpec
import spock.lang.Unroll

import static pl.allegro.experiments.chi.chiserver.utils.SampleStatisticsRequests.sampleClassicStatisticsRequest

class ClassicExperimentStatisticsIntegrationSpec extends BaseE2EIntegrationSpec {

    def "should update experiment goal configuration when new stats are coming"(){
        given:
        def  goal = [
                leadingMetric: 'tx_visit',
                expectedDiffPercent: 2,
                leadingMetricBaselineValue : 5,
                testAlpha : 0.05,
                testPower : 0.85,
                requiredSampleSize: 1000
        ]

        def experiment = startedExperiment([goal: goal])

        def newStats = sampleClassicStatisticsRequest([
                experimentId: experiment.id,
                metricName  : "tx_visit",
                variantName:  "base",
                device:       "all",
                data        : [
                    value : 0.025,
                    count : 877500
                ]
        ])

        when:
        postClassicStatistics(newStats)
        experiment = fetchExperiment(experiment.id as String)

        then:
        with(experiment.goal) {
            println it
            assert it.leadingMetric == 'tx_visit'
            assert it.expectedDiffPercent == 2
            assert it.leadingMetricBaselineValue == 2.5
            assert it.testAlpha == 0.05
            assert it.testPower == 0.85
            assert it.requiredSampleSize == 1755000
            assert it.currentSampleSize == 877500
        }
    }

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
        statistics.size() == 1

        and:
        statistics[0].device == "all"
        statistics[0].experimentId == experiment.id
        statistics[0].toDate == '2018-06-01'
        statistics[0].metrics.size() == 2

        and:
        def txDaily = statistics[0].metrics.tx_daily.someVariant
        txDaily.value == 0.07
        txDaily.diff == 0.0003
        txDaily.pValue == 0.3
        txDaily.count == 1684500

        and:
        def gmv = statistics[0].metrics.gmv.someVariant
        gmv.value == 1.23
        gmv.diff == 0.0003
        gmv.pValue == 0.3
        gmv.count == 1684500
    }

    def "should save and return classic statistics for multiple devices"() {
        given:
        def experiment = startedExperiment()
        def requestSmartphone = sampleClassicStatisticsRequest([
                experimentId: experiment.id,
                metricName  : "gmv",
                device      : "phone"
        ])
        def requestAll = sampleClassicStatisticsRequest([
                experimentId: experiment.id,
                metricName  : "gmv",
                device      : "all"
        ])

        when:
        def responseSmartphone = postClassicStatistics(requestSmartphone)
        def responseAll = postClassicStatistics(requestAll)

        then:
        responseSmartphone.statusCode.value() == 200
        responseAll.statusCode.value() == 200

        when:
        def statistics = fetchStatistics(experiment.id as String)
        def statisticsSmartphone = statistics.find {it.device == 'phone'}
        def statisticsAll = statistics.find {it.device == 'all'}

        then:
        statistics.size() == 2

        and:
        statisticsAll.device == "all"
        statisticsAll.experimentId == experiment.id
        statisticsAll.toDate == '2018-06-01'
        statisticsAll.metrics.size() == 1

        and:
        def txDailyAll = statisticsAll.metrics.gmv.someVariant
        txDailyAll.value == 0.07
        txDailyAll.diff == 0.0003
        txDailyAll.pValue == 0.3
        txDailyAll.count == 1684500

        and:
        statisticsSmartphone.device == "phone"
        statisticsSmartphone.experimentId == experiment.id
        statisticsSmartphone.toDate == '2018-06-01'
        statisticsSmartphone.metrics.size() == 1

        and:
        def txDailySmartphone = statisticsSmartphone.metrics.gmv.someVariant
        txDailySmartphone.value == 0.07
        txDailySmartphone.diff == 0.0003
        txDailySmartphone.pValue == 0.3
        txDailySmartphone.count == 1684500
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
                toDate        : '2018-06-01'
        ])

        when:
        def response = postClassicStatistics(request)

        then:
        response.statusCode.value() == 200

        when:
        request = sampleClassicStatisticsRequest([
                experimentId  : experiment.id,
                variantName   : "variant-b",
                toDate        : toDate
        ])

        and:
        response = postClassicStatistics(request)

        then:
        response.statusCode.value() == 200

        when:
        def result = fetchStatistics(experiment.id as String)

        then:
        result.size() == 0

        where:
        description         | toDate
        'date mismatch'     | '2018-06-02'
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
        result.size() == 0
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
        def statistics = fetchStatistics(experiment.id as String)[0]

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
        def statistics = fetchStatistics(experiment.id as String)[0]

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
