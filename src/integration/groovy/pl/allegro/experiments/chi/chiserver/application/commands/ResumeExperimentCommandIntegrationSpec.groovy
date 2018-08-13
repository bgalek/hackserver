package pl.allegro.experiments.chi.chiserver.application.commands

import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.ExperimentCommandException
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.ExperimentNotFoundException
import spock.lang.Unroll

import static pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentStatus.*

class ResumeExperimentCommandIntegrationSpec extends BaseCommandIntegrationSpec {

    def "should resume PAUSED experiment"() {
        given:
        def experiment = pausedExperiment()

        when:
        resumeExperiment(experiment.id)

        then:
        fetchExperiment(experiment.id).isActive()
    }

    def "should not resume non existing experiment"() {
        when:
        resumeExperiment('nonexistentExperimentId')

        then:
        thrown ExperimentNotFoundException
    }

    @Unroll
    def "should not resume #status experiment"() {
        given:
        def experiment = experimentWithStatus(status)

        when:
        resumeExperiment(experiment.id as String)

        then:
        def exception = thrown ExperimentCommandException
        exception.message == "Experiment <${experiment.id}> is not PAUSED."

        and:
        fetchExperiment(experiment.id).status == status

        where:
        status << allExperimentStatusValuesExcept(PAUSED)
    }
}
