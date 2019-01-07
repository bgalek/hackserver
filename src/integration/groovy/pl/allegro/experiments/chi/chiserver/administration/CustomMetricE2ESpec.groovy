package pl.allegro.experiments.chi.chiserver.administration

import org.springframework.http.HttpStatus
import org.springframework.web.client.HttpClientErrorException
import pl.allegro.experiments.chi.chiserver.BaseE2EIntegrationSpec
import pl.allegro.experiments.chi.chiserver.utils.ApiExperimentUtils
import spock.lang.Unroll


class CustomMetricE2ESpec extends BaseE2EIntegrationSpec implements ApiExperimentUtils  {
    def "should create customMetric in experiment"() {
        when:
        def experiment = draftExperiment([variantNames: ["v1", "v2"], customMetricDefinition: [
                    metricName: "customMetricDefinition",
                    definitionForVariants: [
                        [
                            variantName: "v1",
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
                        ],
                        [
                            variantName: "v2",
                            viewEventDefinition: [
                                    category: "category3",
                                    action: "action3",
                                    value: "value3",
                                    label: "label3",
                                    boxName: "boxName3"
                            ],
                            successEventDefinition: [
                                    category: "category4",
                                    action: "action4",
                                    value: "value4",
                                    label: "label4",
                                    boxName: "boxName4"
                            ]
                        ]
                    ]
        ]])

        then:
        with (experiment.customMetricDefinition) {
            metricName == "customMetricDefinition"
            with (definitionForVariants[0]) {
                variantName == "v1"
                viewEventDefinition.category == "category1"
                viewEventDefinition.action == "action1"
                viewEventDefinition.value == "value1"
                viewEventDefinition.label == "label1"
                viewEventDefinition.boxName == "boxName1"
                successEventDefinition.category == "category2"
                successEventDefinition.action == "action2"
                successEventDefinition.value == "value2"
                successEventDefinition.label == "label2"
                successEventDefinition.boxName == "boxName2"
            }
            with (definitionForVariants[1]) {
                variantName == "v2"
                viewEventDefinition.category == "category3"
                viewEventDefinition.action == "action3"
                viewEventDefinition.value == "value3"
                viewEventDefinition.label == "label3"
                viewEventDefinition.boxName == "boxName3"
                successEventDefinition.category == "category4"
                successEventDefinition.action == "action4"
                successEventDefinition.value == "value4"
                successEventDefinition.label == "label4"
                successEventDefinition.boxName == "boxName4"
            }
        }
    }

    @Unroll
    def "should create customMetric with empty strings if #fieldCase field provided"() {
        when:
        def experiment = draftExperiment([variantNames: ["v1", "v2"], customMetricDefinition: [
                metricName: "customMetricDefinition",
                definitionForVariants: [
                        [
                                variantName: "v1",
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
                                        label: null,
                                        boxName: "boxName2"
                                ]
                        ],
                        [
                                variantName: "v2",
                                viewEventDefinition: [
                                        category: "category3",
                                        action: "action3",
                                        value: "value3",
                                        label: "label3",
                                        boxName: ""
                                ],
                                successEventDefinition: [
                                        category: "category4",
                                        action: "action4",
                                        value: "value4",
                                        label: "label4",
                                        boxName: ""
                                ]
                        ]
                ]
        ]])

        then:
        experiment.customMetricDefinition == expectedResult

        where:
        expectedResult = [
                metricName: "customMetricDefinition",
                definitionForVariants: [
                        [
                                variantName: "v1",
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
                                        label: "",
                                        boxName: "boxName2"
                                ]
                        ],
                        [
                                variantName: "v2",
                                viewEventDefinition: [
                                        category: "category3",
                                        action: "action3",
                                        value: "value3",
                                        label: "label3",
                                        boxName: ""
                                ],
                                successEventDefinition: [
                                        category: "category4",
                                        action: "action4",
                                        value: "value4",
                                        label: "label4",
                                        boxName: ""
                                ]
                        ]
                ]
        ]

        fieldCase << ['null', 'empty']
    }

    @Unroll
    def "should not create experiment if missing #missingCase in request"() {
        when:
        draftExperiment([variantNames: ["v1"], customMetricDefinition: customMetricDefinition])

        then:
        def exception = thrown HttpClientErrorException
        exception.statusCode == HttpStatus.BAD_REQUEST

        where:
        customMetricDefinition << [
                [
                        definitionForVariants: [
                                [
                                        variantName: "v1",
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
                                ]
                        ]
                ],
                [
                        metricName: "customMetricDefinition",
                        definitionForVariants: [
                                [
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
                                ]
                        ]
                ],
                [
                        metricName: "customMetricDefinition",
                        definitionForVariant: [
                                [
                                        variantName: "v1",
                                        successEventDefinition: [
                                                category: "category2",
                                                action: "action2",
                                                value: "value2",
                                                label: "label2",
                                                boxName: "boxName2"
                                        ]
                                ]
                        ]
                ],
                [
                        metricName: "customMetricDefinition",
                        definitionForVariants: [
                                [
                                        variantName: "v1",
                                        viewEventDefinition: [
                                                category: "category1",
                                                action: "action1",
                                                value: "value1",
                                                label: "label1",
                                                boxName: "boxName1"
                                        ]
                                ]
                        ]
                ],

        ]

        missingCase << ["metricName", "variantName", "viewEventDefinition", "successEventDefinition"]
    }

    def "should create experiment without customMetrics if not provided"() {
        when:
        def experiment = draftExperiment()

        then:
        ! experiment.customMetricDefinition
    }

    def "should not create experiment if is not defined for all variants"() {
        when:
        draftExperiment([variantNames: ["v1", "v2", "v3"], customMetricDefinition: [
                metricName: "customMetricDefinition",
                definitionForVariants: [
                        [
                                variantName: "v1",
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
                        ],
                        [
                                variantName: "v2",
                                viewEventDefinition: [
                                        category: "category3",
                                        action: "action3",
                                        value: "value3",
                                        label: "label3",
                                        boxName: "boxName3"
                                ],
                                successEventDefinition: [
                                        category: "category4",
                                        action: "action4",
                                        value: "value4",
                                        label: "label4",
                                        boxName: "boxName4"
                                ]
                        ]
                ]
        ]])

        then:
        def exception = thrown HttpClientErrorException
        exception.statusCode == HttpStatus.BAD_REQUEST
    }

    def "should not create experiment if custom metric is defined for more variants than exists"() {
        when:
        draftExperiment([variantNames: ["v1", "v2"], customMetricDefinition: [
                metricName: "customMetricDefinition",
                definitionForVariants: [
                        [
                                variantName: "v1",
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
                        ],
                        [
                                variantName: "v2",
                                viewEventDefinition: [
                                        category: "category3",
                                        action: "action3",
                                        value: "value3",
                                        label: "label3",
                                        boxName: "boxName3"
                                ],
                                successEventDefinition: [
                                        category: "category4",
                                        action: "action4",
                                        value: "value4",
                                        label: "label4",
                                        boxName: "boxName4"
                                ]
                        ],
                        [
                                variantName: "v3",
                                viewEventDefinition: [
                                        category: "category5",
                                        action: "action5",
                                        value: "value5",
                                        label: "label5",
                                        boxName: "boxName5"
                                ],
                                successEventDefinition: [
                                        category: "category6",
                                        action: "action6",
                                        value: "value6",
                                        label: "label6",
                                        boxName: "boxName6"
                                ]
                        ]
                ]
        ]])

        then:
        def exception = thrown HttpClientErrorException
        exception.statusCode == HttpStatus.BAD_REQUEST
    }

}



