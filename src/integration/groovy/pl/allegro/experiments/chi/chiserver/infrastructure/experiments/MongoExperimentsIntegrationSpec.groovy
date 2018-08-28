package pl.allegro.experiments.chi.chiserver.infrastructure.experiments

import org.javers.common.exception.JaversException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import pl.allegro.experiments.chi.chiserver.BaseIntegrationSpec
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentDefinition
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository

class MongoExperimentsIntegrationSpec extends BaseIntegrationSpec {

    @Autowired
    ExperimentsRepository experimentsRepository

    @Autowired
    MongoTemplate mongoTemplate

    def "should get simple experiments saved before"() {
        given:
        def experiment = exampleExperiment()

        when:
        experimentsRepository.save(experiment)

        then:
        experimentsRepository.getExperiment(experiment.id).get() == experiment
    }

    def "should remove experiments not tracked by javers"() {
        given:
        def experiment = exampleExperiment()
        mongoTemplate.save(experiment)

        when:
        experimentsRepository.delete(experiment.id)

        then:
        notThrown JaversException

        and:
        !experimentsRepository.getExperiment(experiment.id).isPresent()
    }

    ExperimentDefinition exampleExperiment() {
        ExperimentDefinition.builder()
                .id(UUID.randomUUID().toString())
                .variantNames(['base', 'v1'])
                .percentage(10)
                .groups([])
                .build()
    }
}
