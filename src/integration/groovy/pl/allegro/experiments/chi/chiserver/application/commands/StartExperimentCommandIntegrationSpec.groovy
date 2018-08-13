package pl.allegro.experiments.chi.chiserver.application.commands

import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.ExperimentCommandException
import spock.lang.Unroll

import static pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentStatus.*

class StartExperimentCommandIntegrationSpec extends BaseCommandIntegrationSpec {

    def "should start DRAFT experiment"() {
        given:
        def experiment = draftExperiment()

        when:
        startExperiment(experiment.id, 30)

        then:
        fetchExperiment(experiment.id)isActive()
    }

    @Unroll
    def "should not start #status experiment"() {
        given:
        def experiment = experimentWithStatus(status)

        when:
        startExperiment(experiment.id, 14)

        then:
        def exception = thrown ExperimentCommandException
        exception.message == "Experiment is not DRAFT: ${experiment.id}"

        and:
        experiment.status == status

        where:
        status << allExperimentStatusValuesExcept(DRAFT)
    }

    @Unroll
    def "should not start experiment when given number of days is #duration"() {
        given:
        def experiment = draftExperiment()

        when:
        startExperiment(experiment.id, duration)

        then:
        thrown IllegalArgumentException

        and:
        !fetchExperiment(experiment.id).isActive()

        where:
        duration << [0, -1]
    }
}