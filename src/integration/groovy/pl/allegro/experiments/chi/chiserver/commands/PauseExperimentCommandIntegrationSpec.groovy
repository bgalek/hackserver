package pl.allegro.experiments.chi.chiserver.commands

import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.ExperimentCommandException
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.ExperimentNotFoundException
import spock.lang.Unroll

import static pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentStatus.*

class PauseExperimentCommandIntegrationSpec extends BaseCommandIntegrationSpec {

    def "should pause ACTIVE experiment"() {
        given:
        def experiment = startedExperiment()

        when:
        pauseExperiment(experiment.id)

        then:
        fetchExperiment(experiment.id).isPaused()
    }

    def "should not pause nonexistent experiment"() {
        when:
        pauseExperiment('nonexistentExperimentId')

        then:
        thrown ExperimentNotFoundException
    }

    @Unroll
    def "should not pause #status experiment"() {
        given:
        def experiment = experimentWithStatus(status)

        when:
        pauseExperiment(experiment.id as String)

        then:
        def exception = thrown ExperimentCommandException
        exception.message == "Experiment is not ACTIVE. Now '${experiment.id}' has $status status"

        and:
        fetchExperiment(experiment.id).status == status

        where:
        status << allExperimentStatusValuesExcept(ACTIVE)
    }
}
