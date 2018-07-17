package pl.allegro.experiments.chi.chiserver.infrastructure.statistics

import com.google.gson.Gson
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestTemplate
import pl.allegro.experiments.chi.chiserver.BaseIntegrationSpec
import pl.allegro.experiments.chi.chiserver.domain.experiments.DeviceClass
import pl.allegro.experiments.chi.chiserver.domain.statistics.classic.ClassicExperimentStatistics
import pl.allegro.experiments.chi.chiserver.domain.statistics.classic.VariantStatistics
import spock.lang.Unroll

import java.time.LocalDate

class ClassicExperimentStatisticsIntegrationSpec extends BaseIntegrationSpec {

    @Autowired
    ClassicStatisticsRepository classicStatisticsRepository

    @Autowired
    MongoTemplate mongoTemplate

    @Autowired
    RestTemplate restTemplate

    @Autowired
    Gson jsonConverter;

    def "should save and return classic statistics"() {
        given:
        def expId = "exp_" + UUID.randomUUID().toString()

        when:
        ResponseEntity response = postStats(statsFromPyspark(expId: expId, metric: "tx_daily", toDate: '2018-06-01', value: 0.07, diff: 0.0003, pValue: 0.3, count: 1684500))

        then:
        response.statusCode.value() == 200

        when:
        response = postStats(statsFromPyspark(expId: expId, metric: "gmv", toDate: '2018-06-01', value: 1.23, diff: 0.0003, pValue: 0.3, count: 1684500))

        then:
        response.statusCode.value() == 200

        when:
        ResponseEntity<String> resultEntity = getStats(expId, DeviceClass.all)
        ClassicExperimentStatistics result = jsonConverter.fromJson(resultEntity.body, ClassicExperimentStatistics.class)

        then:
        resultEntity.statusCode == HttpStatus.OK
        result.device == DeviceClass.all
        result.experimentId == expId
        result.toDate == LocalDate.parse('2018-06-01')
        result.metrics.size() == 2

        VariantStatistics txStats = result.getMetricStatistics("tx_daily").someVariant
        txStats.value == 0.07
        txStats.diff == 0.0003
        txStats.pValue == 0.3
        txStats.count == 1684500

        VariantStatistics gmvStats = result.getMetricStatistics("gmv").someVariant
        gmvStats.value == 1.23
        gmvStats.diff == 0.0003
        gmvStats.pValue == 0.3
        gmvStats.count == 1684500
    }

    @Unroll
    def "should return nothing on #testCase mismatch"() {
        when:
        ResponseEntity response = postStats(statsFromPyspark(expId: expId, variant: "variant-a", toDate: '2018-06-01', durationMillis: 691200000L))

        then:
        response.statusCode.value() == 200

        when:
        response = postStats(statsFromPyspark(expId: expId, variant: "variant-b", toDate: toDate, durationMillis: durationMillis))

        then:
        response.statusCode.value() == 200

        when:
        ResponseEntity<String> resultEntity = getStats(expId, DeviceClass.all)

        then:
        resultEntity.statusCode == HttpStatus.NO_CONTENT

        where:
        testCase   | expId                                 | toDate       | durationMillis
        'date'     | 'exp_' + UUID.randomUUID().toString() | '2018-06-02' | 691200000L
        'duration' | 'exp_' + UUID.randomUUID().toString() | '2018-06-01' | 961200000L
    }

    def "should fail on variant mismatch"() {
        given:
        def expId = "exp_" + UUID.randomUUID().toString()

        when:
        ResponseEntity response = postStats(statsFromPyspark(expId: expId, metric: "tx_daily", variant: "variant-a"))

        then:
        response.statusCode.value() == 200

        when:
        response = postStats(statsFromPyspark(expId: expId, metric: "gmv", variant: "variant-b"))

        then:
        response.statusCode.value() == 200

        when:
        classicStatisticsRepository.getExperimentStatistics(expId, DeviceClass.all).get()

        then:
        thrown RuntimeException
    }

    def "should evict old stats when new are coming"() {
        given:
        def expId = "exp_no_1" + UUID.randomUUID().toString()

        when:
        postStats(statsFromPyspark(expId: expId, metric: 'gmv', variant: 'variant-a', toDate: '2018-01-01', value: 1.11, diff: 2.011))
        postStats(statsFromPyspark(expId: expId, metric: 'gmv', variant: 'variant-b', toDate: '2018-01-01', value: 1.11, diff: 2.011))

        postStats(statsFromPyspark(expId: expId, metric: 'gmv', variant: 'variant-a', toDate: '2018-01-01', value: 0.11, diff: 0.011))
        postStats(statsFromPyspark(expId: expId, metric: 'gmv', variant: 'variant-b', toDate: '2018-01-01', value: 0.12, diff: 0.012))

        postStats(statsFromPyspark(expId: expId, metric: 'gmv', variant: 'variant-a', toDate: '2018-01-02', value: 0.13, diff: 0.013))
        postStats(statsFromPyspark(expId: expId, metric: 'gmv', variant: 'variant-b', toDate: '2018-01-02', value: 0.14, diff: 0.014))

        ResponseEntity<String> resultEntity = getStats(expId, DeviceClass.all)
        ClassicExperimentStatistics result = jsonConverter.fromJson(resultEntity.body, ClassicExperimentStatistics.class)

        then:
        resultEntity.statusCode == HttpStatus.OK
        result.device == DeviceClass.all
        result.experimentId == expId
        result.toDate == LocalDate.parse('2018-01-02')
        result.metrics.size() == 1
        result.getMetricStatistics("gmv").size() == 2

        VariantStatistics variantA = result.getMetricStatistics("gmv").'variant-a'
        VariantStatistics variantB = result.getMetricStatistics("gmv").'variant-b'

        variantA.value == 0.13
        variantA.diff == 0.013

        variantB.value == 0.14
        variantB.diff == 0.014
    }

    def "should update existing stats with new variants and return classic statistics"() {
        given:
        def expId = "exp_" + UUID.randomUUID().toString()

        when:
        def response = postStats(statsFromPyspark(expId: expId, variant: 'variant-a', value: 0.1, toDate: '2018-06-01'))

        then:
        response.statusCode.value() == 200

        when:
        def secondResponse = postStats(statsFromPyspark(expId: expId, variant: 'variant-b', value: 0.2, toDate: '2018-06-01'))

        then:
        secondResponse.statusCode.value() == 200

        when:
        ResponseEntity<String> resultEntity = getStats(expId, DeviceClass.all)
        ClassicExperimentStatistics result = jsonConverter.fromJson(resultEntity.body, ClassicExperimentStatistics.class)

        then:
        resultEntity.statusCode == HttpStatus.OK
        result.device == DeviceClass.all
        result.experimentId == expId
        result.toDate == LocalDate.parse('2018-06-01')
        result.metrics.size() == 1
        result.getMetricStatistics("gmv").size() == 2

        def variantA = result.getMetricStatistics("gmv").'variant-a'
        variantA.value == 0.1

        def variantB = result.getMetricStatistics("gmv").'variant-b'
        variantB.value == 0.2
    }

    ResponseEntity<String> getStats(String expId, DeviceClass deviceClass) {
        def headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)

        return restTemplate.getForEntity(localUrl('/api/statistics/' + expId), String.class, Map.of("device", deviceClass))
    }

    ResponseEntity postStats(String stats) {
        def headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)
        def entity = new HttpEntity(stats, headers)

        return restTemplate.postForEntity(localUrl('/api/statistics'), entity, String)
    }

    String statsFromPyspark(Map<String, Object> data = [:]) {
        String expId = data['expId'] as String ?: UUID.randomUUID().toString()
        long durationMillis = data['durationMillis'] as Long ?: 691200000L
        String toDate = data['toDate'] as String ?: "2018-06-01"
        String device = data['device'] as String ?: "all"
        String variant = data['variant'] as String ?: "someVariant"
        String metric = data['metric'] as String ?: "gmv"
        double value = data['value'] as Double ?: 0.07
        double diff = data['diff'] as Double ?: 0.0003
        double pValue = data['pValue'] as Double ?: 0.3
        long count = data['count'] as Long ?: 1684500

        """{
            "experimentId": "$expId",
            "durationMillis": $durationMillis,
            "toDate": "$toDate",
            "device": "$device",
            "variantName": "$variant",
            "metricName": "$metric", 
            "data": {
                "value": $value,
                "diff": $diff,
                "pValue": $pValue,
                "count": $count
             }
        }
        """
    }
}
