package pl.allegro.experiments.chi.chiserver.infrastructure.experiments

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import pl.allegro.experiments.chi.chiserver.BaseIntegrationSpec
import pl.allegro.experiments.chi.chiserver.domain.experiments.ActivityPeriod
import pl.allegro.experiments.chi.chiserver.domain.experiments.Experiment
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentVariant
import pl.allegro.experiments.chi.chiserver.domain.experiments.HashRangePredicate
import pl.allegro.experiments.chi.chiserver.domain.experiments.PercentageRange

import java.time.Clock
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

class MongoExperimentsIntegrationSpec extends BaseIntegrationSpec {

    @Autowired
    MongoExperimentsRepository mongoExperimentsRepository

    @Autowired
    MongoTemplate mongoTemplate

    def "should get simple experiments saved before"() {
        given:
        def experiment = new Experiment(
                "some", [
                    new ExperimentVariant("base", [new HashRangePredicate(new PercentageRange(0, 10))]),
                    new ExperimentVariant("v2", [new HashRangePredicate(new PercentageRange(10, 20))]),
                ],
                "exciting stuff", "exciting link", "tester", [], false,
                new ActivityPeriod(someDateTime().minusDays(2), someDateTime().plusDays(3)),
                null,
                null
        )

        when:
        mongoExperimentsRepository.save(experiment)

        then:
        mongoExperimentsRepository.getExperiment("some") == experiment
    }

    def someDateTime() {
        ZonedDateTime.now(Clock.systemUTC()).truncatedTo(ChronoUnit.SECONDS)
    }

    def cleanup() {
        mongoTemplate.dropCollection("experiments")
    }
}
