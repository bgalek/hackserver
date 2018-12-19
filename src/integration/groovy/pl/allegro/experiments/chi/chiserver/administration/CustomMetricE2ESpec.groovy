package pl.allegro.experiments.chi.chiserver.administration

import org.springframework.http.HttpStatus
import org.springframework.web.client.HttpClientErrorException
import pl.allegro.experiments.chi.chiserver.BaseE2EIntegrationSpec
import pl.allegro.experiments.chi.chiserver.utils.ApiExperimentUtils
import spock.lang.Unroll


class CustomMetricE2ESpec extends BaseE2EIntegrationSpec implements ApiExperimentUtils  {
    def "should create customMetric in experiment"() {
        when:
        def experiment = draftExperiment([customMetricDefinition: [
                name: "customMetricDefinition",
                viewEventDefinition: [
                        category: "category1",
                        action: "action1",
                        value: "value1",
                        label: "label1",
                        boxName: "boxName1"
                ],
                successEventDefinition: [
                        category: "category2",
                        action: "action2",
                        value: "value2",
                        label: "label2",
                        boxName: "boxName2"
                ]
        ]])

        then:
        experiment.customMetricDefinition.name == "customMetricDefinition"
        experiment.customMetricDefinition.viewEventDefinition.category == "category1"
        experiment.customMetricDefinition.viewEventDefinition.action == "action1"
        experiment.customMetricDefinition.viewEventDefinition.value == "value1"
        experiment.customMetricDefinition.viewEventDefinition.label == "label1"
        experiment.customMetricDefinition.viewEventDefinition.boxName == "boxName1"
        experiment.customMetricDefinition.successEventDefinition.category == "category2"
        experiment.customMetricDefinition.successEventDefinition.action == "action2"
        experiment.customMetricDefinition.successEventDefinition.value == "value2"
        experiment.customMetricDefinition.successEventDefinition.label == "label2"
        experiment.customMetricDefinition.successEventDefinition.boxName == "boxName2"
    }

    @Unroll
    def "should create customMetric with missing fields if #fieldCase field provided"() {
        when:
        def experiment = draftExperiment([customMetricDefinition: customMetricDefinition])

        then:
        experiment.customMetricDefinition == expectedResult

        where:
        customMetricDefinition << [
                [
                        name: "customMetricDefinition",
                        viewEventDefinition: [
                            category: "category1",
                            action: "action1",
                            value: "value1",
                            label: null,
                            boxName: "boxName1"
                    ],
                    successEventDefinition: [
                            category: "category2",
                            action: "action2",
                            value: "value2",
                            label: "label2",
                            boxName: null
                    ]
                ],
                [
                        name: "customMetricDefinition",
                        viewEventDefinition: [
                                category: "category1",
                                action: "action1",
                                value: "value1",
                                label: "",
                                boxName: "boxName1"
                        ],
                        successEventDefinition: [
                                category: "category2",
                                action: "action2",
                                value: "value2",
                                label: "label2",
                                boxName: ""
                        ]
                ],

        ]

        expectedResult << [
                [
                        name: "customMetricDefinition",

                        viewEventDefinition: [
                                category: "category1",
                                action: "action1",
                                value: "value1",
                                label: "",
                                boxName: "boxName1"
                        ],
                        successEventDefinition: [
                                category: "category2",
                                action: "action2",
                                value: "value2",
                                label: "label2",
                                boxName: ""
                        ]
                ],
                [
                        name: "customMetricDefinition",

                        viewEventDefinition: [
                                category: "category1",
                                action: "action1",
                                value: "value1",
                                label: "",
                                boxName: "boxName1"
                        ],
                        successEventDefinition: [
                                category: "category2",
                                action: "action2",
                                value: "value2",
                                label: "label2",
                                boxName: ""
                        ]
                ]
        ]

        fieldCase << ['null', 'empty']
    }

    def "should return 400 response code if not proper customMetricDefinition provided"() {
        when:
        draftExperiment([customMetricDefinition: customMetricDefinition])

        then:
        def exception = thrown HttpClientErrorException
        exception.statusCode == HttpStatus.BAD_REQUEST

        where:
        customMetricDefinition << [
                [
                        viewEventDefinition: [
                                category: "category1",
                                action: "action1",
                                value: "value1",
                                label: null,
                                boxName: "boxName1"
                        ],
                        successEventDefinition: [
                                category: "category2",
                                action: "action2",
                                value: "value2",
                                label: "label2",
                                boxName: null
                        ]
                ],
                [
                        name: "customMetricDefinition",
                        successEventDefinition: [
                                category: "category2",
                                action: "action2",
                                value: "value2",
                                label: "label2",
                                boxName: ""
                        ]
                ],
                [
                        name: "customMetricDefinition",
                        viewEventDefinition: [
                                category: "category1",
                                action: "action1",
                                value: "value1",
                                label: null,
                                boxName: "boxName1"
                        ]
                ],

        ]


    }
    def "should create experiment without customMetric if not provided"() {
        when:
        def experiment = draftExperiment()
        then:
        !(experiment.hasProperty("customMetricDefinition"))
    }

}



