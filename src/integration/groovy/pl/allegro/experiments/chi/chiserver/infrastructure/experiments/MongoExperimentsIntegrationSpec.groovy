package pl.allegro.experiments.chi.chiserver.infrastructure.experiments

import org.javers.common.exception.JaversException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import pl.allegro.experiments.chi.chiserver.BaseIntegrationSpec
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentDefinition

class MongoExperimentsIntegrationSpec extends BaseIntegrationSpec {

    static EXPERIMENTS_COLLECTION = MongoExperimentsRepository.COLLECTION

    @Autowired
    MongoExperimentsRepository mongoExperimentsRepository

    @Autowired
    MongoTemplate mongoTemplate

    def "should get simple experiments saved before"() {
        given:
        def experiment = exampleExperiment()

        when:
        mongoExperimentsRepository.save(experiment)

        then:
        mongoExperimentsRepository.getExperiment(experiment.id).get() == experiment
    }

    def "should remove experiments not tracked by javers"() {
        given:
        def experiment = exampleExperiment()
        mongoTemplate.save(experiment, EXPERIMENTS_COLLECTION)

        when:
        mongoExperimentsRepository.delete(experiment.id)

        then:
        notThrown JaversException

        and:
        !mongoExperimentsRepository.getExperiment(experiment.id).isPresent()
    }

    ExperimentDefinition exampleExperiment() {
        ExperimentDefinition.builder()
                .id(UUID.randomUUID().toString())
                .variantNames(['base', 'v1'])
                .percentage(10)
                .groups([])
                .build()
    }

    def cleanup() {
        mongoTemplate.dropCollection(EXPERIMENTS_COLLECTION)
    }
}
