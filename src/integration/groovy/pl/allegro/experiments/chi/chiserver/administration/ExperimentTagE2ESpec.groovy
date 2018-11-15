package pl.allegro.experiments.chi.chiserver.administration

import org.springframework.web.client.HttpClientErrorException
import pl.allegro.experiments.chi.chiserver.BaseE2EIntegrationSpec

class ExperimentTagE2ESpec extends BaseE2EIntegrationSpec {
    def "should create experiment tag"() {
        when:
        def tagId1 = createExperimentTag()
        def tagId2 = createExperimentTag()

        then:
        fetchAllExperimentTags().collect {it -> it.id}  == [tagId1, tagId2]
    }

    def "should create experiment with tags"() {
        given:
        def tagId1 = createExperimentTag()
        def tagId2 = createExperimentTag()

        when:
        def createdExperiment = draftExperiment([tags: [tagId1, tagId2]])

        then:
        createdExperiment.tags.collect {it -> it.id} == [tagId1, tagId2]
    }

    def "should not create experiment if provided tag does not exist"() {
        given:
        def tagId1 = createExperimentTag()

        when:
        draftExperiment([tags: ['nonexistent', tagId1]])

        then:
        thrown(HttpClientErrorException)
    }
}
