package pl.allegro.experiments.chi.chiserver.commands

import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.ExperimentCommandException
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.ExperimentNotFoundException
import spock.lang.Unroll

import java.time.ZonedDateTime

import static pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentStatus.*

class StopExperimentCommandIntegrationSpec extends BaseCommandIntegrationSpec {

    def "should stop active experiment"() {
        given:
        def experiment = experimentWithStatus(ACTIVE)

        when:
        stopExperiment(experiment.id)

        then:
        def fresh = fetchExperiment(experiment.id)

        fresh.isEnded()
        ZonedDateTime.now().compareTo(fresh.activeTo) >= 0
    }

    def "should stop full-on experiment"() {
        given:
        def experiment = experimentWithStatus(FULL_ON)

        when:
        stopExperiment(experiment.id)

        then:
        def fresh = fetchExperiment(experiment.id)

        fresh.isEnded()
        fresh.activeTo == experiment.activeTo
    }

    def "should not stop nonexistent experiment"() {
        given:
        def nonexistentExperimentId = 'nonexistent experiment id'

        when:
        stopExperiment(nonexistentExperimentId)

        then:
        def exception = thrown ExperimentNotFoundException
        exception.message == "Experiment not found: $nonexistentExperimentId"
    }

    @Unroll
    def "should not stop #status experiment"() {
        given:
        def experiment = experimentWithStatus(status)

        when:
        stopExperiment(experiment.id)

        then:
        def exception = thrown ExperimentCommandException
        exception.message == "$status experiment cannot be ended"

        where:
        status << allExperimentStatusValuesExcept(ACTIVE, FULL_ON)
    }
}
