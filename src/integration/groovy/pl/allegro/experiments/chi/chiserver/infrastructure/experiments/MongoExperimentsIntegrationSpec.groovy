package pl.allegro.experiments.chi.chiserver.infrastructure.experiments

import org.javers.common.exception.JaversException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import pl.allegro.experiments.chi.chiserver.BaseIntegrationSpec
import pl.allegro.experiments.chi.chiserver.utils.ExperimentFactory

class MongoExperimentsIntegrationSpec extends BaseIntegrationSpec {

    static EXPERIMENTS_COLLECTION = MongoExperimentsRepository.COLLECTION

    @Autowired
    MongoExperimentsRepository mongoExperimentsRepository

    @Autowired
    MongoTemplate mongoTemplate

    def "should get simple experiments saved before"() {
        given:
        def experiment = ExperimentFactory.definitionWithId("some")

        when:
        mongoExperimentsRepository.save(experiment)

        then:
        mongoExperimentsRepository.getExperiment('some').get().getDefinition().get() == experiment
    }

    def "should remove not experiments not tracked by javers"() {
        given:
        def experiment = ExperimentFactory.definitionWithId("legacy-experiment")
        mongoTemplate.save(experiment, EXPERIMENTS_COLLECTION)

        when:
        mongoExperimentsRepository.delete(experiment.id)

        then:
        notThrown(JaversException)

        and:
        !mongoExperimentsRepository.getExperiment(experiment.id).isPresent()
    }

    def cleanup() {
        mongoTemplate.dropCollection(EXPERIMENTS_COLLECTION)
    }
}
