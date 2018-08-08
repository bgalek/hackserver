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

import static pl.allegro.experiments.chi.chiserver.utils.SampleInMemoryExperimentsRepository.FULL_ON_TEST_EXPERIMENT_ID
import static pl.allegro.experiments.chi.chiserver.utils.SampleInMemoryExperimentsRepository.TEST_EXPERIMENT_ID

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
        List interactionList = [
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
        ]

        when:
        restTemplate.postForEntity(localUrl('/api/interactions/v1/'), interactionList, List)

        then:
        def interactions = interactionConverter.fromJson(JsonOutput.toJson(interactionList))
        inMemoryInteractionRepository.interactionSaved(interactions[0])
    }

    @Unroll
    def "should not save interactions when connected experiment #description"() {
        given:
        def interactionList = [
                [
                        "userId"         : "someUserId123",
                        "userCmId"       : "someUserCmId123",
                        "experimentId"   : experimentId,
                        "variantName"    : "someVariantName",
                        "internal"       : true,
                        "deviceClass"    : "iphone",
                        "interactionDate": "1970-01-01T00:00:00Z",
                        "appId"          : "a"
                ]
        ]

        when:
        restTemplate.postForEntity(localUrl('/api/interactions/v1/'), interactionList, List)

        then:
        def interactions = interactionConverter.fromJson(JsonOutput.toJson(interactionList))
        !inMemoryInteractionRepository.interactionSaved(interactions[0])

        where:
        experimentId                     | description
        FULL_ON_TEST_EXPERIMENT_ID       | 'is full-on'
        'SOME NONEXISTENT EXPERIMENT ID' | 'does not exist'
    }

    @Unroll
    def "should return 400 when #error"() {
        given:
        def interactionList = List.of(interaction)

        when:
        restTemplate.postForEntity(localUrl('/api/interactions/v1/'), interactionList, List)

        then:
        thrown(HttpClientErrorException)

        where:
        error                       | interaction
        'required field is null'    | interactionWithNulls
        'required field is missing' | interactionWithMissingRequiredField
    }

    @Unroll
    def "should save interactions for #reportingType experiment"() {
        given:
        userProvider.user = new User('Anonymous', [], true)

        def request = [
                id              : experimentId,
                variantNames    : ['v2'],
                percentage      : 10,
                reportingType   : reportingType,
                eventDefinitions: eventDefinitions,
                reportingEnabled: true
        ]

        and:
        restTemplate.postForEntity(localUrl('/api/admin/experiments'), request, Map)

        and:
        def interactionList = [
                [
                        "userId"         : "someUserId123",
                        "userCmId"       : "someUserCmId123",
                        "experimentId"   : experimentId,
                        "variantName"    : "v2",
                        "internal"       : false,
                        "deviceClass"    : "iphone",
                        "interactionDate": "1970-01-01T00:00:00Z",
                        "appId"          : "a"
                ]
        ]

        when:
        restTemplate.postForEntity(localUrl('/api/interactions/v1/'), interactionList, List)

        then:
        def interactions = interactionConverter.fromJson(JsonOutput.toJson(interactionList))
        inMemoryInteractionRepository.interactionSaved(interactions[0])

        where:
        experimentId | reportingType | eventDefinitions
        'e1'         | 'FRONTEND'    | frontendEventDefinitions
        'e2'         | 'GTM'         | null
        'e3'         | 'BACKEND'     | null
    }

    static Map interactionWithNulls = [

            "userId"         : null,
            "userCmId"       : null,
            "experimentId"   : null,
            "variantName"    : null,
            "internal"       : null,
            "deviceClass"    : null,
            "interactionDate": null

    ]

    static Map interactionWithMissingRequiredField = [

            "userId"      : "123",
            "userCmId"    : "123",
            "experimentId": TEST_EXPERIMENT_ID,
            "variantName" : "123",
            "internal"    : true,
            "deviceClass" : "iphone"

    ]

    static List frontendEventDefinitions = [
            [
                    label   : 'label1',
                    category: 'category1',
                    value   : 'value1',
                    action  : 'action1'
            ],
            [
                    label   : 'label2',
                    category: 'category2',
                    value   : 'value2',
                    action  : 'action2'
            ],
    ]
}
