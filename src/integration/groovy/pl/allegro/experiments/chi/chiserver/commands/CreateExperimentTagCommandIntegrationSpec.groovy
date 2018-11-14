package pl.allegro.experiments.chi.chiserver.commands

import org.springframework.beans.factory.annotation.Autowired
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentTagRepository
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.ExperimentTagCreationRequest

class CreateExperimentTagCommandIntegrationSpec extends BaseCommandIntegrationSpec {

    @Autowired
    ExperimentTagRepository experimentTagRepository;

    def "should create experiment tag"() {
        given:
        def request = new ExperimentTagCreationRequest('exampleTag')

        when:
        createExperimentTag(request)

        then:
        experimentTagRepository.get('exampleTag').isPresent()
    }

}
