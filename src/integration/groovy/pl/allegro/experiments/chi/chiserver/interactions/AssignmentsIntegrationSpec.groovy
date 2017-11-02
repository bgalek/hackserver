package pl.allegro.experiments.chi.chiserver.interactions

import groovy.json.JsonOutput
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import pl.allegro.experiments.chi.chiserver.BaseIntegrationSpec
import pl.allegro.experiments.chi.chiserver.interactions.infrastructure.InMemoryInteractionRepository

import java.time.Instant

class AssignmentsIntegrationSpec extends BaseIntegrationSpec {

    @Autowired
    InMemoryInteractionRepository inMemoryInteractionRepository

    RestTemplate restTemplate = new RestTemplate()

    def "should save interactions"() {
        given:
        List<InteractionDto> interactions = [
                sampleInteraction('user-1', 'variant-a'),
                sampleInteraction('user-2', 'variant-b')
        ]

        when:
        restTemplate.exchange(localUrl('/api/interactions/v1/'), HttpMethod.POST, new HttpEntity(new InteractionsDto(interactions)), Void.class)

        then:
        inMemoryInteractionRepository.assertInteractionSaved(interactions[0].toInteraction())
        inMemoryInteractionRepository.assertInteractionSaved(interactions[1].toInteraction())
    }

    def "should throw error when argument is missing"() {
        given:
        String data = JsonOutput.toJson([
                "interactionDtos": [
                        [
                                "userId": "123",
                                "userCmId": "123",
                                "experimentId": "q234",
                                "variantName": "123",
                                "internal": true,
                                "deviceClass": "iphone"
                        ]
                ]
        ])

        when:
        restTemplate.exchange(localUrl('/api/interactions/v1/'), HttpMethod.POST, new HttpEntity<String>(data), Void.class)

        then:
        thrown(HttpClientErrorException)
    }

    def "should throw error when required argument is null"() {
        given:
        String data = JsonOutput.toJson([
                "interactionDtos": [
                        [
                                "userId": null,
                                "userCmId": null,
                                "experimentId": null,
                                "variantName": null,
                                "internal": null,
                                "deviceClass": null,
                                "interactionDate": null
                        ]
                ]
        ])

        when:
        restTemplate.exchange(localUrl('/api/interactions/v1/'), HttpMethod.POST, new HttpEntity<String>(data), Void.class)

        then:
        thrown(HttpClientErrorException)
    }

    InteractionDto sampleInteraction(String userId, String variantName) {
        new InteractionDto(
                userId,
                null,
                'experimentId',
                variantName,
                true,
                'iphone',
                Instant.now())
    }
}
