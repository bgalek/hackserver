package pl.allegro.experiments.chi.chiserver.interactions

import groovy.json.JsonOutput
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import pl.allegro.experiments.chi.chiserver.BaseIntegrationSpec
import pl.allegro.experiments.chi.chiserver.interactions.infrastructure.InMemoryInteractionRepository
import spock.lang.Unroll

class InteractionsIntegrationSpec extends BaseIntegrationSpec {

    @Autowired
    InMemoryInteractionRepository inMemoryInteractionRepository

    InteractionsConverter interactionConverter = new InteractionsConverter()

    RestTemplate restTemplate = new RestTemplate()

    def "should save interactions"() {
        given:
        String interactionsJson = JsonOutput.toJson([
                [
                        "userId"         : "someUserId123",
                        "userCmId"       : "someUserCmId123",
                        "experimentId"   : "someExperimentId123",
                        "variantName"    : "someVariantName",
                        "internal"       : true,
                        "deviceClass"    : "iphone",
                        "interactionDate": "1970-01-01T00:00:00Z",
                        "appId"          : "a"
                ],
                [
                        "userId"         : null,
                        "userCmId"       : null,
                        "experimentId"   : "anotherExperimentId",
                        "variantName"    : "variant",
                        "internal"       : null,
                        "deviceClass"    : null,
                        "interactionDate": "1970-01-01T00:00:00Z"
                ],
                [
                        "experimentId"   : "anotherExperimentId",
                        "variantName"    : "variant",
                        "interactionDate": "1970-01-01T00:00:00Z"
                ]
        ])

        when:
        restTemplate.exchange(localUrl('/api/interactions/v1/'),
                HttpMethod.POST, httpJsonEntity(interactionsJson), Void.class)


        then:
        def interactions = interactionConverter.fromJson(interactionsJson)
        inMemoryInteractionRepository.interactionSaved(interactions[0])
        inMemoryInteractionRepository.interactionSaved(interactions[1])
        inMemoryInteractionRepository.interactionSaved(interactions[2])
    }

    @Unroll
    def "should return 400 when #error"() {
        when:
        restTemplate.exchange(localUrl('/api/interactions/v1/'), HttpMethod.POST, httpJsonEntity(data), Void.class)

        then:
        thrown(HttpClientErrorException)

        where:
        data << [
                JsonOutput.toJson([
                        [
                                "userId"         : null,
                                "userCmId"       : null,
                                "experimentId"   : null,
                                "variantName"    : null,
                                "internal"       : null,
                                "deviceClass"    : null,
                                "interactionDate": null
                        ]
                ]),
                JsonOutput.toJson([
                        [
                                "userId"      : "123",
                                "userCmId"    : "123",
                                "experimentId": "q234",
                                "variantName" : "123",
                                "internal"    : true,
                                "deviceClass" : "iphone"
                        ]
                ])
        ]
        error << ["required field is null", "required field is missing"]

    }

    HttpHeaders headers() {
        HttpHeaders headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8)
        headers
    }

    HttpEntity httpJsonEntity(String jsonBody) {
        new HttpEntity<String>(jsonBody, headers())
    }
}
