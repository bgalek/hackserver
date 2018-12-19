package pl.allegro.experiments.chi.chiserver.administration

import pl.allegro.experiments.chi.chiserver.BaseE2EIntegrationSpec
import pl.allegro.experiments.chi.chiserver.utils.ApiExperimentUtils


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
        experiment.customMetricDefinition.successEventDefinition.value == "value2"
        experiment.customMetricDefinition.successEventDefinition.label == "label2"
        experiment.customMetricDefinition.successEventDefinition.boxName == "boxName2"
    }


}



