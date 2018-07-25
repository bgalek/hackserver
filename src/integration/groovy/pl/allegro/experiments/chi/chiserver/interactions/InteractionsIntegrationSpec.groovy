package pl.allegro.experiments.chi.chiserver.interactions

import groovy.json.JsonOutput
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpMethod
import org.springframework.test.context.ContextConfiguration
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import pl.allegro.experiments.chi.chiserver.BaseIntegrationSpec
import pl.allegro.experiments.chi.chiserver.domain.User
import pl.allegro.experiments.chi.chiserver.domain.UserProvider
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository
import pl.allegro.experiments.chi.chiserver.domain.interactions.InteractionConverter
import pl.allegro.experiments.chi.chiserver.infrastructure.interactions.InMemoryInteractionRepository
import spock.lang.Unroll

import static pl.allegro.experiments.chi.chiserver.utils.SampleInMemoryExperimentsRepository.TEST_EXPERIMENT_ID
import static pl.allegro.experiments.chi.chiserver.utils.SampleInMemoryExperimentsRepository.TEST_EXPERIMENT_ID_WITH_DISABLED_REPORTING

@ContextConfiguration(classes = [InteractionsIntegrationTestConfig])
class InteractionsIntegrationSpec extends BaseIntegrationSpec {

    @Autowired
    InMemoryInteractionRepository inMemoryInteractionRepository

    @Autowired
    InteractionConverter interactionConverter

    @Autowired
    UserProvider userProvider

    RestTemplate restTemplate = new RestTemplate()

    @Autowired
    ExperimentsRepository experimentsRepository

    def "should save interactions"() {
        given:
        String interactionsJson = JsonOutput.toJson([
                [
                        "userId"         : "someUserId123",
                        "userCmId"       : "someUserCmId123",
                        "experimentId"   : TEST_EXPERIMENT_ID,
                        "variantName"    : "someVariantName",
                        "internal"       : true,
                        "deviceClass"    : "iphone",
                        "interactionDate": "1970-01-01T00:00:00Z",
                        "appId"          : "a"
                ]
        ])

        when:
        restTemplate.exchange(localUrl('/api/interactions/v1/'),
                HttpMethod.POST, httpJsonEntity(interactionsJson), Void.class)


        then:
        def interactions = interactionConverter.fromJson(interactionsJson)
        inMemoryInteractionRepository.interactionSaved(interactions[0])
    }

    @Unroll
    def "should not save interactions when #error"() {
        when:
        restTemplate.exchange(localUrl('/api/interactions/v1/'),
                HttpMethod.POST, httpJsonEntity(interaction), Void.class)

        then:
        def interactions = interactionConverter.fromJson(interaction)
        !inMemoryInteractionRepository.interactionSaved(interactions[0])

        where:
        interaction << [
                JsonOutput.toJson([[
                        "userId"         : "someUserId123",
                        "userCmId"       : "someUserCmId123",
                        "experimentId"   : "SOME NONEXISTENT EXPERIMENT ID",
                        "variantName"    : "someVariantName",
                        "internal"       : true,
                        "deviceClass"    : "iphone",
                        "interactionDate": "1970-01-01T00:00:00Z",
                        "appId"          : "a"
                ]]),
                JsonOutput.toJson([[
                        "userId"         : "someUserId123",
                        "userCmId"       : "someUserCmId123",
                        "experimentId"   : TEST_EXPERIMENT_ID_WITH_DISABLED_REPORTING,
                        "variantName"    : "someVariantName",
                        "internal"       : true,
                        "deviceClass"    : "iphone",
                        "interactionDate": "1970-01-01T00:00:00Z",
                        "appId"          : "a"
                ]])
        ]
        error << ['there is no connected experiment', 'experiment reporting is disabled']
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
                                "experimentId": TEST_EXPERIMENT_ID,
                                "variantName" : "123",
                                "internal"    : true,
                                "deviceClass" : "iphone"
                        ]
                ])
        ]
        error << ["required field is null", "required field is missing"]

    }

}
