package pl.allegro.experiments.chi.chiserver.infrastructure.experiments

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import pl.allegro.experiments.chi.chiserver.BaseIntegrationSpec
import pl.allegro.experiments.chi.chiserver.utils.ExperimentFactory

class MongoExperimentsIntegrationSpec extends BaseIntegrationSpec {

    @Autowired
    MongoExperimentsRepository mongoExperimentsRepository

    @Autowired
    MongoTemplate mongoTemplate

    def "should get simple experiments saved before"() {
        given:
        def experiment = ExperimentFactory.experimentWithId("some")

        when:
        mongoExperimentsRepository.save(experiment)

        then:
        mongoExperimentsRepository.getExperiment("some") == experiment
    }

    def cleanup() {
        mongoTemplate.dropCollection("experiments")
    }
}
