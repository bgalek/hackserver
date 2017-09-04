package pl.allegro.tech.selfservice.newservicesingle

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.client.RestTemplate
import pl.allegro.tech.selfservice.newservicesingle.domain.DomainObject

class ExampleIntegrationTest extends BaseIntegrationTest {

    @Autowired
    RestTemplate restTemplate

    def "should respond with 'public resource' when asking for public resources"() {
        when:
        def response = restTemplate.getForEntity(localUrl('/service/resources/public'), DomainObject)

        then:
        with(response) {
            statusCode.value() == 200
            body.value == 'public resource'
        }
    }

    def "should respond to ping"() {
        when:
        def response = restTemplate.postForEntity(
                localUrl('/service/resources/pong'),
                new DomainObject("ping!", 100),
                DomainObject
        )

        then:
        with(response) {
            statusCode.value() == 200
            body.value == 'ping! pong!'
            body.ttl == 99
        }
    }
}