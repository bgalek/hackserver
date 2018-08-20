package pl.allegro.experiments.chi.chiserver.interactions

import groovy.json.JsonOutput
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.web.client.HttpClientErrorException
import pl.allegro.experiments.chi.chiserver.BaseE2EIntegrationSpec
import pl.allegro.experiments.chi.chiserver.domain.interactions.InteractionConverter
import pl.allegro.experiments.chi.chiserver.infrastructure.interactions.InMemoryInteractionRepository
import spock.lang.Unroll

import static pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentStatus.FULL_ON

@ContextConfiguration(classes = [InteractionsIntegrationTestConfig])
class InteractionsIntegrationSpec extends BaseE2EIntegrationSpec {

    @Autowired
    InMemoryInteractionRepository inMemoryInteractionRepository

    @Autowired
    InteractionConverter interactionConverter

    @Unroll
    def "should save interactions when connected experiment is #status"() {
        given:
        def experiment = experimentWithStatus(status)

        List interactionList = [
                [
                        "userId"         : "someUserId123",
                        "userCmId"       : "someUserCmId123",
                        "experimentId"   : experiment.id,
                        "variantName"    : "someVariantName",
                        "internal"       : true,
                        "deviceClass"    : "iphone",
                        "interactionDate": "1970-01-01T00:00:00Z",
                        "appId"          : "a"
                ]
        ]

        when:
        postInteractions(interactionList)

        then:
        def interactions = interactionConverter.fromJson(JsonOutput.toJson(interactionList))
        inMemoryInteractionRepository.interactionSaved(interactions[0])

        where:
        status << allExperimentStatusValuesExcept(FULL_ON)
    }

    def "should not save interactions when connected experiment does not exist"() {
        given:
        def interactionList = [
                [
                        "userId"         : "someUserId123",
                        "userCmId"       : "someUserCmId123",
                        "experimentId"   : 'SOME NONEXISTENT EXPERIMENT ID',
                        "variantName"    : "someVariantName",
                        "internal"       : true,
                        "deviceClass"    : "iphone",
                        "interactionDate": "1970-01-01T00:00:00Z",
                        "appId"          : "a"
                ]
        ]

        when:
        postInteractions(interactionList)

        then:
        def interactions = interactionConverter.fromJson(JsonOutput.toJson(interactionList))
        !inMemoryInteractionRepository.interactionSaved(interactions[0])
    }

    def "should not save interactions when connected experiment is FULL_ON"() {
        given:
        def experiment = fullOnExperiment()

        and:
        def interactionList = [
                [
                        "userId"         : "someUserId123",
                        "userCmId"       : "someUserCmId123",
                        "experimentId"   : experiment.id,
                        "variantName"    : "someVariantName",
                        "internal"       : true,
                        "deviceClass"    : "iphone",
                        "interactionDate": "1970-01-01T00:00:00Z",
                        "appId"          : "a"
                ]
        ]

        when:
        postInteractions(interactionList)

        then:
        def interactions = interactionConverter.fromJson(JsonOutput.toJson(interactionList))
        !inMemoryInteractionRepository.interactionSaved(interactions[0])
    }

    @Unroll
    def "should return 400 when #error"() {
        given:
        def interactionList = List.of(interaction)

        when:
        postInteractions(interactionList)

        then:
        thrown HttpClientErrorException

        where:
        error                       | interaction
        'required field is null'    | interactionWithNulls
        'required field is missing' | interactionWithMissingRequiredField
    }

    @Unroll
    def "should save interactions for #reportingType experiment"() {
        given:
        def experiment = draftExperiment([
                variantNames    : ['v2'],
                percentage      : 10,
                reportingType   : reportingType,
                eventDefinitions: eventDefinitions,
                reportingEnabled: true
        ])

        and:
        def interactionList = [
                [
                        "userId"         : "someUserId123",
                        "userCmId"       : "someUserCmId123",
                        "experimentId"   : experiment.id,
                        "variantName"    : "v2",
                        "internal"       : false,
                        "deviceClass"    : "iphone",
                        "interactionDate": "1970-01-01T00:00:00Z",
                        "appId"          : "a"
                ]
        ]

        when:
        postInteractions(interactionList)

        then:
        def interactions = interactionConverter.fromJson(JsonOutput.toJson(interactionList))
        inMemoryInteractionRepository.interactionSaved(interactions[0])

        where:
        reportingType | eventDefinitions
        'FRONTEND'    | frontendEventDefinitions
        'GTM'         | null
        'BACKEND'     | null
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
            "experimentId": UUID.randomUUID().toString(),
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
