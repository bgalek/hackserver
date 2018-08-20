package pl.allegro.experiments.chi.chiserver.infrastructure.experiments.fetch

import pl.allegro.experiments.chi.chiserver.BaseE2EIntegrationSpec

class GetAllExperimentGroupE2ESpec extends BaseE2EIntegrationSpec {

    def "should return all experiment groups"() {
        given:
        def firstExperiment = startedExperiment()
        def secondExperiment = draftExperiment()
        def group = experimentGroup([firstExperiment.id, secondExperiment.id])

        expect:
        fetchExperimentGroup(group.id as String) == [
                id         : group.id,
                salt       : firstExperiment.id,
                experiments: [firstExperiment.id, secondExperiment.id]
        ]
    }
}
