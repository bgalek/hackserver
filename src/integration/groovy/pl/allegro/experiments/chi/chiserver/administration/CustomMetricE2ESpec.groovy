package pl.allegro.experiments.chi.chiserver.administration

import pl.allegro.experiments.chi.chiserver.BaseE2EIntegrationSpec
import pl.allegro.experiments.chi.chiserver.domain.experiments.CustomMetricDefinition
import pl.allegro.experiments.chi.chiserver.domain.experiments.EventDefinition

class CustomMetricE2ESpec extends BaseE2EIntegrationSpec {
    def "should create customMetric in experiment"() {
        given:
        def cmd = new CustomMetricDefinition(
                "customMetricDefinition",
                new EventDefinition('category1', 'action1', 'value1', 'label1', 'boxName1'),
                new EventDefinition('category2', 'action2', 'value2', 'label2', 'boxName2')
        )
        def experiment = draftExperiment([customMetricDefinition : cmd])

        when:
        experiment = fetchExperiments().find{it.id = experiment.id} as Map
        then:
        experiment.customMetricDefinition.name == "customMetricDefinition"
        experiment.customMetricDefinition.viewEventDefinition.category == "category1"
        experiment.customMetricDefinition.viewEventDefinition.action == "action1"
        experiment.customMetricDefinition.successEventDefinition.value == "value2"
        experiment.customMetricDefinition.successEventDefinition.label == "label2"
        experiment.customMetricDefinition.successEventDefinition.boxName == "boxName2"
    }


}



