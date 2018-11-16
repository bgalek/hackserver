package pl.allegro.experiments.chi.chiserver.administration

import org.springframework.http.HttpStatus
import org.springframework.web.client.HttpClientErrorException
import pl.allegro.experiments.chi.chiserver.BaseE2EIntegrationSpec
import pl.allegro.experiments.chi.chiserver.domain.User
import spock.lang.Unroll

import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

import static org.springframework.http.HttpStatus.BAD_REQUEST
import static pl.allegro.experiments.chi.chiserver.domain.experiments.DeviceClass.*
import static pl.allegro.experiments.chi.chiserver.utils.SampleExperimentRequests.sampleExperimentCreationRequestProperties

class ExperimentsSelfServiceE2ESpec extends BaseE2EIntegrationSpec {

    @Unroll
    def "should set editable flag depending on who ask for single experiment/multiple experiments"() {
        given:
        signInAs(new User('Author', [], true))
        def experiment = draftExperiment([groups: ['group a', 'group b']])

        when:
        signInAs(user)
        experiment = fetchExperiment(experiment.id as String)

        then:
        experiment.editable == editable

        when:
        experiment = fetchExperiments().find{ it.id == experiment.id } as Map

        then:
        experiment.editable == editable

        where:
        user                                    | editable
        new User('Author', [], true)            | true
        new User('Author', [], false)           | true
        new User('Author', ['group a'], false)  | true
        new User('Author', ['group a'], true)   | true
        new User('Author', [], false)           | true
        new User('Unknown', [], false)          | false
        new User('Unknown', ['group a'], false) | true
        new User('Unknown', [], true)           | true
        new User('Unknown', ['group a'], true)  | true
    }

    def "should execute all administration commands"() {
        given:
        def properties = sampleExperimentCreationRequestProperties([
                description        : 'desc',
                documentLink       : 'https://vuetifyjs.com/vuetify/quick-start',
                variantNames       : ['v2', 'v3'],
                internalVariantName: 'v1',
                percentage         : 10,
                deviceClass        : 'phone',
                groups             : ['group a', 'group b'],
                reportingEnabled   : true,
                reportingType: 'FRONTEND',
                eventDefinitions: [],
                tags: []
        ])

        when:
        def experiment = draftExperiment(properties)

        then:
        def expectedExperiment = [
                id              : properties.id,
                author          : 'Root',
                status          : 'DRAFT',
                measurements    : [lastDayVisits: 0],
                editable        : true,
                groups          : ['group a', 'group b'],
                description     : 'desc',
                documentLink    : 'https://vuetifyjs.com/vuetify/quick-start',
                reportingEnabled: true,
                reportingType   : 'FRONTEND',
                eventDefinitions: [],
                renderedVariants        : [
                        [
                                name      : 'v1',
                                predicates: [[type: 'INTERNAL'],               [type: 'DEVICE_CLASS', device: 'phone']]
                        ],
                        [
                                name      : 'v2',
                                predicates: [[type: 'HASH', from: 0, to: 10],  [type: 'DEVICE_CLASS', device: 'phone']]
                        ],
                        [
                                name      : 'v3',
                                predicates: [[type: 'HASH', from: 50, to: 60], [type: 'DEVICE_CLASS', device: 'phone']]
                        ]
                ],
                variantNames       : ['v2', 'v3'],
                internalVariantName: 'v1',
                deviceClass        : 'phone',
                percentage         : 10,
                maxPossibleAllocation: 50,
                tags: []
        ]
        experiment == expectedExperiment
        experiment.definition == expectedExperiment.definition
        experiment.variants == expectedExperiment.variants

        and:
        def experiments = fetchExperiments()
        experiments.contains(expectedExperiment)

        when:
        startExperiment(experiment.id as String, 30)

        then:
        fetchExperiment(experiment.id as String).status == "ACTIVE"

        when:
        def tags = [createExperimentTag(), createExperimentTag()]
        updateExperimentDescriptions(experiment.id as String, 'chi rulez', 'new link', ['group c'], tags)
        experiment = fetchExperiment(experiment.id as String)

        then:
        experiment.description == 'chi rulez'
        experiment.documentLink == 'new link'
        experiment.groups == ['group c']

        when:
        updateExperimentVariants(experiment.id as String, 'internV', 18, 'phone')
        experiment = fetchExperiment(experiment.id as String)

        then:
        experiment.percentage == 18
        experiment.internalVariantName == 'internV'
        experiment.variantNames == ['v2', 'v3']
        experiment.deviceClass == 'phone'

        when:
        updateExperimentEventDefinitions(experiment.id as String, [
                [
                        boxName: 'b1',
                        action: 'a1',
                        category: 'c1',
                        label: 'l1',
                        value: 'v1'
                ]
        ])

        then:
        fetchExperiment(experiment.id as String).eventDefinitions == [
                [
                        boxName: 'b1',
                        action: 'a1',
                        category: 'c1',
                        label: 'l1',
                        value: 'v1'
                ]
        ]

        when:
        pauseExperiment(experiment.id as String)

        then:
        fetchExperiment(experiment.id as String).status == "PAUSED"

        when:
        resumeExperiment(experiment.id as String)

        then:
        fetchExperiment(experiment.id as String).status == "ACTIVE"

        when:
        def activityPeriod = fetchExperiment(experiment.id as String).activityPeriod as Map
        def currentActiveTo = ZonedDateTime.parse(activityPeriod.activeTo as String)
        prolongExperiment(experiment.id as String, 30)

        then:
        def formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX")
        def expectedActiveTo = currentActiveTo.plusDays(30).format(formatter)
        fetchExperiment(experiment.id as String).activityPeriod.activeTo == expectedActiveTo

        when:
        stopExperiment(experiment.id as String)

        then:
        fetchExperiment(experiment.id as String).status == "ENDED"

        when:
        pauseExperiment(experiment.id as String)

        then:
        def exception = thrown HttpClientErrorException
        exception.statusCode == BAD_REQUEST

        and:
        deleteExperiment(experiment.id as String)

        when:
        fetchExperiment(experiment.id as String)

        then:
        exception = thrown(HttpClientErrorException)
        exception.statusCode == HttpStatus.NOT_FOUND
    }

    def "should execute full-on command on active experiment"() {
        given:
        def experiment = startedExperiment([variantNames: ['v1', 'v2']])

        when:
        makeExperimentFullOn(experiment.id as String, 'v1')
        experiment = fetchExperiment(experiment.id as String)

        then:
        def activeTo = ZonedDateTime.parse(experiment.activityPeriod.activeTo as String)

        experiment.status == 'FULL_ON'
        ZonedDateTime.now().compareTo(activeTo) >= 0
        experiment.lastStatusChange == experiment.activityPeriod.activeTo
        experiment.renderedVariants == [
                [
                        name      : 'v1',
                        predicates: [[type: 'FULL_ON']]
                ]
        ]
        experiment.variantNames    == ['v1', 'v2']
        experiment.percentage      == 0
        !experiment.deviceClass
    }

    @Unroll
    def "should execute full-on command on active experiment with #deviceClass"() {
        given:
        def experiment = startedExperiment([variantNames: ['v1', 'v2'], deviceClass: deviceClass])

        when:
        makeExperimentFullOn(experiment.id as String, 'v1')
        experiment = fetchExperiment(experiment.id as String)

        then:
        def activeTo = ZonedDateTime.parse(experiment.activityPeriod.activeTo as String)

        experiment.status == 'FULL_ON'
        ZonedDateTime.now().compareTo(activeTo) >= 0
        experiment.lastStatusChange == experiment.activityPeriod.activeTo
        experiment.renderedVariants == [
                [
                        name      : 'v1',
                        predicates: [
                                [type: 'FULL_ON'],
                                [type: 'DEVICE_CLASS', device: deviceClass.toJsonString()]
                        ]
                ]
        ]
        experiment.variantNames    == ['v1', 'v2']
        experiment.percentage      == 0
        experiment.deviceClass     == deviceClass.toJsonString()

        where:
        deviceClass << [phone, phone_android, phone_iphone, desktop, tablet]
    }
}

