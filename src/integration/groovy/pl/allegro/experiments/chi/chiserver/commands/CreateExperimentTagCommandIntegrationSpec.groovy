package pl.allegro.experiments.chi.chiserver.commands

import org.springframework.beans.factory.annotation.Autowired
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentTagRepository
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.ExperimentCommandException

class CreateExperimentTagCommandIntegrationSpec extends BaseCommandIntegrationSpec {

    @Autowired
    ExperimentTagRepository experimentTagRepository;

    def "should create experiment tag"() {
        when:
        def tagId = createExperimentTag()

        then:
        experimentTagRepository.get(tagId).isPresent()
    }

    def "should not create experiment tag if already exists"() {
        given:
        def existingTagId = createExperimentTag()

        when:
        createExperimentTag(existingTagId)

        then:
        def exception = thrown(ExperimentCommandException)
        exception.message == "Experiment tag $existingTagId already exists"
    }
}
