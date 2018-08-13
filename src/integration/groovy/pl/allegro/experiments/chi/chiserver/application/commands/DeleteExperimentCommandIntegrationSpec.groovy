package pl.allegro.experiments.chi.chiserver.application.commands

import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.ExperimentCommandException

class DeleteExperimentCommandIntegrationSpec extends BaseCommandIntegrationSpec {

    def "should delete experiment"() {
        given:
        def experiment = draftExperiment()

        and:
        statisticsRepository.statisticsDoNotExist(experiment.id)

        when:
        deleteExperiment(experiment.id)

        then:
        !experimentsExists(experiment.id)
    }

    def "should not delete experiment with statistics"() {
        given:
        def experiment = draftExperiment()

        and:
        statisticsRepository.statisticsExist(experiment.id)

        when:
        deleteExperiment(experiment.id)

        then:
        def exception = thrown ExperimentCommandException
        exception.message == "Experiment with statistics cannot be deleted: $experiment.id"

        and:
        experimentsExists(experiment.id)
    }
}