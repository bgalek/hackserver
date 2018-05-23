package pl.allegro.experiments.chi.chiserver.infrastructure.statistics

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestTemplate
import pl.allegro.experiments.chi.chiserver.BaseIntegrationSpec
import pl.allegro.experiments.chi.chiserver.domain.statistics.bayes.BayesianStatisticsRepository
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
        def response = postStats(bayesFromPyspark(expId, 'variant-a', '2018-04-01', [-0.2, -0.1, 0.1, 0.2], [100,  200,  250, 150]))

        then:
        response.statusCode.value() == 200

        when:
        BayesianExperimentStatistics result =
                bayesianStatisticsRepository.experimentStatistics(expId, 'all').get()

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

    def "should evict old stats when new are coming"(){
      given:
      def expId = "exp_no_1" + UUID.randomUUID().toString()

      when:
      postStats(bayesFromPyspark(expId, 'variant-a', '2018-01-01',[1], [1]))
      postStats(bayesFromPyspark(expId, 'variant-b', '2018-01-01',[2], [2]))
      postStats(bayesFromPyspark(expId, 'variant-a', '2018-01-02',[10], [15]))
      postStats(bayesFromPyspark(expId, 'variant-b', '2018-01-02',[20], [25]))

      def result = bayesianStatisticsRepository.experimentStatistics(expId, 'all').get()

      then:
      result.device == 'all'
      result.experimentId == expId
      result.toDate == '2018-01-02'
      result.variantBayesianStatistics.size() == 2

      def variantA = result.variantBayesianStatistics.find{it.variantName == 'variant-a'}
      def variantB = result.variantBayesianStatistics.find{it.variantName == 'variant-b'}

      variantA.samples.values == [10]
      variantA.samples.counts == [15]

      variantB.samples.values == [20]
      variantB.samples.counts == [25]
    }

    def "should update existing stats with new variants and return bayesian statistics"() {
        given:
        def expId = "exp_" + UUID.randomUUID().toString()

        when:
        def response = postStats(bayesFromPyspark(expId, 'variant-a', [-0.2, -0.1], [100,  200]))

        then:
        response.statusCode.value() == 200

        when:
        def secondResponse = postStats(bayesFromPyspark(expId, 'variant-c', [-0.1, -0.2, 0.3, 0.6], [10,  200,  250, 1]))

        then:
        secondResponse.statusCode.value() == 200
        
        when:
        def result = bayesianStatisticsRepository.experimentStatistics(expId, 'all').get()

        then:
        result.device == 'all'
        result.experimentId == expId
        result.toDate == '2018-04-01'
        result.variantBayesianStatistics.size() == 2

        def varianta = result.variantBayesianStatistics.find{it.variantName == 'variant-a'}
        varianta.samples.values == [-0.2, -0.1]
        varianta.samples.counts == [100,  200]
        varianta.outliersLeft == 10
        varianta.outliersRight == 122
        varianta.allCount() == 100 + 200 + 10 + 122

        def variantc = result.variantBayesianStatistics.find{it.variantName == 'variant-c'}
        variantc.samples.values == [-0.1, -0.2, 0.3, 0.6]
        variantc.samples.counts == [10,  200,  250, 1]
        variantc.outliersLeft == 10
        variantc.outliersRight == 122
        variantc.allCount() == 593
    }

    ResponseEntity postStats(String stats) {
        def headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)
        def entity = new HttpEntity(stats, headers)

        restTemplate.postForEntity(localUrl('/api/bayes/statistics'), entity, String)
    }

    String bayesFromPyspark(String expId, String variantName, String toDate, List<Double> values, List<Integer> counts) {
        """{
            'experimentId': '$expId',
            'toDate': '$toDate',
            'device': 'all',
            'variantName': '$variantName',
            'data': {
                    'samples': {
                        'values': $values,
                        'counts': $counts
                    },
                    'outliersLeft': 10,
                    'outliersRight': 122
             }
        }
        """
    }

    String bayesFromPyspark(String expId, String variantName, List<Double> values, List<Integer> counts) {
        bayesFromPyspark(expId, variantName, '2018-04-01', values, counts)
    }
}
