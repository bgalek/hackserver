package pl.allegro.experiments.chi.chiserver.interactions

import groovy.json.JsonOutput
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.test.context.ContextConfiguration
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import pl.allegro.experiments.chi.chiserver.BaseIntegrationSpec
import pl.allegro.experiments.chi.chiserver.infrastructure.InMemoryExperimentsRepository
import pl.allegro.experiments.chi.chiserver.interactions.infrastructure.InMemoryInteractionRepository
import spock.lang.Unroll

@ContextConfiguration(classes = [InteractionsIntegrationTestConfig])
class InteractionsIntegrationSpec extends BaseIntegrationSpec {

    @Autowired
    InMemoryInteractionRepository inMemoryInteractionRepository

    @Autowired
    InMemoryExperimentsRepository experimentsRepository

    InteractionsConverter interactionConverter = new InteractionsConverter()

    RestTemplate restTemplate = new RestTemplate()

    def "should save interactions"() {
        given:
        String interactionsJson = JsonOutput.toJson([
                [
                        "userId"         : "someUserId123",
                        "userCmId"       : "someUserCmId123",
                        "experimentId"   : InteractionsIntegrationTestConfig.TEST_EXPERIMENT_ID,
                        "variantName"    : "someVariantName",
                        "internal"       : true,
                        "deviceClass"    : "iphone",
                        "interactionDate": "1970-01-01T00:00:00Z",
                        "appId"          : "a"
                ],
                [
                        "experimentId"   : InteractionsIntegrationTestConfig.TEST_EXPERIMENT_ID,
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
    }

    def "should not save interactions when there is no connected experiment"() {
        given:
        String interactionsJson = JsonOutput.toJson([
                [
                        "userId"         : "someUserId123",
                        "userCmId"       : "someUserCmId123",
                        "experimentId"   : "SOME NONEXISTENT EXPERIMENT ID",
                        "variantName"    : "someVariantName",
                        "internal"       : true,
                        "deviceClass"    : "iphone",
                        "interactionDate": "1970-01-01T00:00:00Z",
                        "appId"          : "a"
                ],
                [
                        "experimentId"   : "SOME NONEXISTENT EXPERIMENT ID",
                        "variantName"    : "variant",
                        "interactionDate": "1970-01-01T00:00:00Z"
                ]
        ])

        when:
        restTemplate.exchange(localUrl('/api/interactions/v1/'),
                HttpMethod.POST, httpJsonEntity(interactionsJson), Void.class)


        then:
        def interactions = interactionConverter.fromJson(interactionsJson)
        !inMemoryInteractionRepository.interactionSaved(interactions[0])
        !inMemoryInteractionRepository.interactionSaved(interactions[1])
    }

    def "should not save interactions when experiment reporting is disabled"() {
        given:
        String interactionsJson = JsonOutput.toJson([
                [
                        "userId"         : "someUserId123",
                        "userCmId"       : "someUserCmId123",
                        "experimentId"   : InteractionsIntegrationTestConfig.TEST_EXPERIMENT_ID_WITH_DISABLED_REPORTING,
                        "variantName"    : "someVariantName",
                        "internal"       : true,
                        "deviceClass"    : "iphone",
                        "interactionDate": "1970-01-01T00:00:00Z",
                        "appId"          : "a"
                ],
                [
                        "experimentId"   : InteractionsIntegrationTestConfig.TEST_EXPERIMENT_ID_WITH_DISABLED_REPORTING,
                        "variantName"    : "variant",
                        "interactionDate": "1970-01-01T00:00:00Z"
                ]
        ])

        when:
        restTemplate.exchange(localUrl('/api/interactions/v1/'),
                HttpMethod.POST, httpJsonEntity(interactionsJson), Void.class)


        then:
        def interactions = interactionConverter.fromJson(interactionsJson)
        !inMemoryInteractionRepository.interactionSaved(interactions[0])
        !inMemoryInteractionRepository.interactionSaved(interactions[1])
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
                                "experimentId": InteractionsIntegrationTestConfig.TEST_EXPERIMENT_ID,
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
