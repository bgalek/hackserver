package pl.allegro.experiments.chi.chiserver.infrastructure.statistics

import pl.allegro.experiments.chi.chiserver.BaseIntegrationSpec

/**
 * @author bartosz.walacik
 */
class ClassicExperimentStatisticsIntegrationSpec extends BaseIntegrationSpec {

    def "should save and return classic statistics"() {
        when:


        println(statsFromPyspark("e1","v1","tx_daily","2018-06-01", 691200000))

        then:
        false
    }

    String statsFromPyspark(String expId, String variantName, String metricName, String toDate,
                            long duration) {
        """{
            "experimentId": "$expId",
            "durationMillis": $duration,
            "toDate": "$toDate",
            "device": "all",
            "variantName": "$variantName",
            "metricName": "$metricName" 
            "data": {
                "value": 0.07066310942173004,
                "diff": 0.0002925666340161115,
                "pValue": 0.2945983111858368,
                "count": 1684514
             }
        }
        """
    }
}
