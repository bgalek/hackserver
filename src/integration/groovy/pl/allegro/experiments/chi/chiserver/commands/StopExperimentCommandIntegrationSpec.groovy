package pl.allegro.experiments.chi.chiserver.commands

import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.ExperimentCommandException
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.ExperimentNotFoundException
import spock.lang.Unroll

import static pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentStatus.*

class StopExperimentCommandIntegrationSpec extends BaseCommandIntegrationSpec {

    @Unroll
    def "should stop #status experiment"() {
        given:
        def experiment = experimentWithStatus(status)

        when:
        stopExperiment(experiment.id)

        then:
        fetchExperiment(experiment.id).isEnded()

        where:
        status << [ACTIVE, FULL_ON]
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
