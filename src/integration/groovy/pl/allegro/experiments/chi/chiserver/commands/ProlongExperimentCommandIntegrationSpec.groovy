package pl.allegro.experiments.chi.chiserver.commands

import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.ExperimentCommandException

import static pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentStatus.*

class ProlongExperimentCommandIntegrationSpec extends BaseCommandIntegrationSpec {

    def "should prolong ACTIVE experiment"() {
        given:
        def experiment = startedExperiment()

        when:
        prolongExperiment(experiment.id, 30)

        then:
        fetchExperiment(experiment.id).activeTo == experiment.activeTo.plusDays(30)
        fetchExperiment(experiment.id).activeFrom == experiment.activeFrom
    }

    def "should not prolong #status experiment"() {
        given:
        def experiment = experimentWithStatus(status)

        when:
        prolongExperiment(experiment.id, 30)

        then:
         def exception = thrown ExperimentCommandException
        exception.message == "Experiment cant be prolonged if it is not ACTIVE"

        where:
        status << allExperimentStatusValuesExcept(ACTIVE)
    }

    def "should not prolong experiment when given by #duration days"() {
        def experiment = startedExperiment()

        when:
        prolongExperiment(experiment.id, duration)

        then:
        thrown IllegalArgumentException

        where:
        duration << [0, -1]
    }
}