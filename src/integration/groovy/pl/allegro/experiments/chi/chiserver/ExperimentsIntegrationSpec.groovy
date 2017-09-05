package pl.allegro.experiments.chi.chiserver

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.client.RestTemplate

class ExperimentsIntegrationSpec extends BaseIntegrationSpec {

    @Autowired
    RestTemplate restTemplate

    def "should return list of experiments"() {
        when:
        def response = restTemplate.getForEntity(localUrl('/api/experiments'), List)

        then:
        with(response) {
            statusCode.value() == 200
            body.value == 'public resource'
        }
    }
}
