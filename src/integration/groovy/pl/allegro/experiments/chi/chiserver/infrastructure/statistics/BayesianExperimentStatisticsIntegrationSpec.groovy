package pl.allegro.experiments.chi.chiserver.infrastructure.statistics

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.client.RestTemplate
import pl.allegro.experiments.chi.chiserver.BaseIntegrationSpec
import pl.allegro.experiments.chi.chiserver.domain.statistics.BayesianStatisticsRepository
import pl.allegro.experiments.chi.chiserver.domain.statistics.bayes.BayesianExperimentStatistics

class BayesianExperimentStatisticsIntegrationSpec extends BaseIntegrationSpec {

    @Autowired
    BayesianStatisticsRepository bayesianStatisticsRepository

    @Autowired
    MongoTemplate mongoTemplate

    @Autowired
    RestTemplate restTemplate

    def "should save and return bayesian statistics"() {
        given:
        def expId = "exp_" + UUID.randomUUID().toString()

        when:
        def headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)
        def entity = new HttpEntity(bayesFromPyspark(expId), headers)
        def response = restTemplate.postForEntity(localUrl('/api/bayes/statistics'), entity, String)

        then:
        response.statusCode.value() == 200

        when:
        BayesianExperimentStatistics result =
                bayesianStatisticsRepository.experimentStatistics(expId, 'all', '2018-04-01').get()

        then:
        result.device == 'all'
        result.experimentId == expId
        result.toDate == '2018-04-01'
        result.variantBayesianStatistics.size() == 1

        def variant = result.variantBayesianStatistics[0]
        variant.variantName == 'variant-a'
        variant.samples.values == [-0.2, -0.1, 0.1, 0.2]
        variant.samples.counts == [100,  200,  250, 150]
        variant.outliersLeft == 10
        variant.outliersRight == 122
        variant.allCount() == 100 + 200 + 250 + 150 + 10 + 122
    }

    def "should update existing stats with new variants and return bayesian statistics"() {
        given:
        def expId = "exp_" + UUID.randomUUID().toString()

        when:
        def headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)
        def entity = new HttpEntity(bayesFromPyspark(expId), headers)
        def response = restTemplate.postForEntity(localUrl('/api/bayes/statistics'), entity, String)

        then:
        response.statusCode.value() == 200

        when:
        def secondEntity = new HttpEntity(bayesFromPyspark(expId, 'variant-c', [-0.1, -0.2, 0.3, 0.6], [10,  200,  250, 1]), headers)
        def secondResponse = restTemplate.postForEntity(localUrl('/api/bayes/statistics'), secondEntity, String)

        then:
        secondResponse.statusCode.value() == 200
        
        when:
        BayesianExperimentStatistics result =
                bayesianStatisticsRepository.experimentStatistics(expId, 'all', '2018-04-01').get()

        then:
        result.device == 'all'
        result.experimentId == expId
        result.toDate == '2018-04-01'
        result.variantBayesianStatistics.size() == 2

        def varianta = result.variantBayesianStatistics[0]
        varianta.variantName == 'variant-a'
        varianta.samples.values == [-0.2, -0.1, 0.1, 0.2]
        varianta.samples.counts == [100,  200,  250, 150]
        varianta.outliersLeft == 10
        varianta.outliersRight == 122
        varianta.allCount() == 100 + 200 + 250 + 150 + 10 + 122

        def variantc = result.variantBayesianStatistics[1]
        variantc.variantName == 'variant-c'
        variantc.samples.values == [-0.1, -0.2, 0.3, 0.6]
        variantc.samples.counts == [10,  200,  250, 1]
        variantc.outliersLeft == 10
        variantc.outliersRight == 122
        variantc.allCount() == 100 + 200 + 250 + 150 + 10 + 122
    }

    String bayesFromPyspark(String expId) {
        bayesFromPyspark(expId, 'variant-a', [-0.2, -0.1, 0.1, 0.2], [100,  200,  250, 150])
    }

    String bayesFromPyspark(String expId, String variantName, List<Double> values, List<Integer> counts) {
        """{
            'experimentId': '$expId',
            'toDate': '2018-04-01',
            'device': 'all',
            'variantBayesianStatistics': [
                {
                    'variantName': '$variantName',
                    'samples': {
                        'values': $values,
                        'counts': $counts
                    },
                    'outliersLeft': 10,
                    'outliersRight': 122
                }
            ]
        }
        """
    }
}
