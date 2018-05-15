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

    String bayesFromPyspark(String expId) {
        """{
            'experimentId': '$expId',
            'toDate': '2018-04-01',
            'device': 'all',
            'variantBayesianStatistics': [
                {
                    'variantName': 'variant-a',
                    'samples': {
                        'values': [-0.2, -0.1, 0.1, 0.2],
                        'counts': [100,  200,  250, 150]
                    },
                    'outliersLeft': 10,
                    'outliersRight': 122
                }
            ]
        }
        """
    }
}
