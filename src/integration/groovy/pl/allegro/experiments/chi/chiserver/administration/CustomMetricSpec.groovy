package pl.allegro.experiments.chi.chiserver.administration

import pl.allegro.experiments.chi.chiserver.BaseE2EIntegrationSpec
import pl.allegro.experiments.chi.chiserver.domain.experiments.CustomMetricDefinition
import pl.allegro.experiments.chi.chiserver.domain.experiments.EventDefinition

class CustomMetricSpec extends BaseE2EIntegrationSpec {
    def "should save experiment with custom metric definition and return it for clients"() {
        given:
        def cmd = new CustomMetricDefinition("customMetricDefinition",  new EventDefinition('category', 'action', 'value', 'label', 'boxName'),  new EventDefinition('category', 'action', 'value', 'label', 'boxName'),)

        def experiment = draftExperiment([customMetricDefinition : cmd])

        when:
        def experiment1 = fetchExperiments()
        then:
        experiment.status == "DRAFT"
        def a = experiment
        def b = 0 
    }
}
