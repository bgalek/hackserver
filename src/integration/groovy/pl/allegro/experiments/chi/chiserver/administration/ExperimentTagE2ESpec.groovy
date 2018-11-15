package pl.allegro.experiments.chi.chiserver.administration

import pl.allegro.experiments.chi.chiserver.BaseE2EIntegrationSpec

class ExperimentTagE2ESpec extends BaseE2EIntegrationSpec {
    def "should create experiment tag"() {
        when:
        def tagId1 = createExperimentTag()
        def tagId2 = createExperimentTag()

        then:
        fetchAllExperimentTags().collect {it -> it.id}  == [tagId1, tagId2]
    }
}
