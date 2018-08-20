package pl.allegro.experiments.chi.chiserver.administration

import pl.allegro.experiments.chi.chiserver.BaseE2EIntegrationSpec

class LegacyExperimentReportingDefinitionE2ESpec extends BaseE2EIntegrationSpec {

    def "should append default reporting type to legacy mongo experiments"() {
        given:
        def experiment = draftExperiment()

        expect:
        fetchExperiments().find { it.id == experiment.id }.reportingType == 'BACKEND'
        fetchExperiments().find { it.id == experiment.id }.eventDefinitions == []

        and:
        fetchExperiment(experiment.id as String).reportingType == 'BACKEND'
        fetchExperiment(experiment.id as String).eventDefinitions == []
    }
}
