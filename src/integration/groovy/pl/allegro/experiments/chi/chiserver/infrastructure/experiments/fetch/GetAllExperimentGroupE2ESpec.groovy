package pl.allegro.experiments.chi.chiserver.infrastructure.experiments.fetch

import pl.allegro.experiments.chi.chiserver.BaseE2EIntegrationSpec

class GetAllExperimentGroupE2ESpec extends BaseE2EIntegrationSpec {

    def "should return all experiment groups"() {
        given:
        def firstExperiment = startedExperiment()
        def secondExperiment = draftExperiment()
        def group = createExperimentGroupAndFetch([firstExperiment.id, secondExperiment.id])

        expect:
        def fetchedGroup = fetchExperimentGroup(group.id as String)

        fetchedGroup.id        == group.id
        fetchedGroup.salt      == firstExperiment.id
        fetchedGroup.experiments == [firstExperiment.id, secondExperiment.id]
        fetchedGroup.allocationTable.size() == 4
    }
}
