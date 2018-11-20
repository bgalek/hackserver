package pl.allegro.experiments.chi.chiserver.administration

import pl.allegro.experiments.chi.chiserver.BaseE2EIntegrationSpec
import spock.lang.Unroll

//TODO this test os overambitious, should be focused on reporting definition
class ExperimentReportingDefinitionE2E extends BaseE2EIntegrationSpec {

    @Unroll
    def "should preserve reporting definition during #reportingType experiment lifecycle"() {
        given:
        def experiment = draftExperiment([
                id              : experimentId,
                variantNames    : ['v2'],
                percentage      : 10,
                reportingType   : reportingType,
                eventDefinitions: eventDefinitions
        ])

        when:
        startExperiment(experiment.id as String, 30)
        updateExperimentVariants(experiment.id as String, 'internV', 18, 'phone')
        pauseExperiment(experiment.id as String)
        resumeExperiment(experiment.id as String)
        prolongExperiment(experiment.id as String, 30)
        stopExperiment(experiment.id as String)

        and:
        experiment = fetchExperiment(experiment.id as String)
        experiment = fetchExperiments().find{ it.id == experiment.id } as Map

        then:
        experiment.reportingType == expectedReportingType
        experiment.eventDefinitions as Set == expectedEventDefinitions as Set

        and:
        experiment.reportingType == expectedReportingType
        experiment.eventDefinitions as Set == expectedEventDefinitions as Set

        where:
        experimentId | reportingType | eventDefinitions       | expectedReportingType | expectedEventDefinitions
        'id_0'       | 'FRONTEND'    | sampleEventDefinitions | 'FRONTEND'            | firstExpectedEventDefinitions
        'id_1'       | 'GTM'         | null                   | 'GTM'                 | secondExpectedEventDefinitions(experimentId)
        'id_2'       | 'BACKEND'     | null                   | 'BACKEND'             | []
        'id_3'       | null          | null                   | 'BACKEND'             | []
    }

    static List sampleEventDefinitions = [
            [
                    label   : 'label1',
                    category: 'category1',
                    value   : 'value1',
                    action  : 'action1',
                    boxName : 'b1'
            ],
            [
                    label   : 'label2',
                    category: 'category2',
                    value   : 'value2',
                    action  : 'action2',
                    boxName : 'b2'
            ],
    ]

    static List firstExpectedEventDefinitions = [
            [
                    label   : 'label1',
                    category: 'category1',
                    value   : 'value1',
                    action  : 'action1',
                    boxName : 'b1'
            ],
            [
                    label   : 'label2',
                    category: 'category2',
                    value   : 'value2',
                    action  : 'action2',
                    boxName : 'b2'
            ]
    ]

    static List secondExpectedEventDefinitions(String experimentId) {
        [
                [
                        category: 'chiInteraction',
                        action  : experimentId,
                        label   : 'v2',
                        value   : '',
                        boxName : ''
                ]
        ]
    }
}


