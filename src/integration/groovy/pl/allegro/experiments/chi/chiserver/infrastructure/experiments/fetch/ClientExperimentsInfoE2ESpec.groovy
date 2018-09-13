package pl.allegro.experiments.chi.chiserver.infrastructure.experiments.fetch

import pl.allegro.experiments.chi.chiserver.BaseE2EIntegrationSpec

class ClientExperimentsInfoE2ESpec extends BaseE2EIntegrationSpec {

    def "should return basic experiments info"() {
        given:
        def experiment = fullOnExperiment()

        when:
        def experimentsInfo = fetchExperimentsInfo()

        then:
        experimentsInfo[0].name == experiment.id
        experimentsInfo[0].variants.containsAll(["base", "v1"])
    }
}