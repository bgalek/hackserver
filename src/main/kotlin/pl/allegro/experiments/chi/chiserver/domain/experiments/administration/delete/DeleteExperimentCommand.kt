package pl.allegro.experiments.chi.chiserver.domain.experiments.administration.delete

import pl.allegro.experiments.chi.chiserver.application.experiments.administration.AuthorizationException
import pl.allegro.experiments.chi.chiserver.domain.UserProvider
import pl.allegro.experiments.chi.chiserver.domain.experiments.Experiment
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentId
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.ExperimentNotFoundException
import pl.allegro.experiments.chi.chiserver.domain.statistics.StatisticsRepository


class DeleteExperimentCommand(private val experimentsRepository: ExperimentsRepository,
                              private val userProvider: UserProvider,
                              private val experimentId: ExperimentId,
                              private val statisticsRepository: StatisticsRepository) {

    fun execute() {
        val experiment = experimentsRepository.getExperiment(experimentId)
        validate(experiment)
        experimentsRepository.delete(experimentId)
    }

    private fun validate(experiment: Experiment?) {
        if (experiment == null) {
            throw ExperimentNotFoundException("Experiment not found: ${experimentId}")
        }

        val user = userProvider.getCurrentUser()
        if (!user.isOwner(experiment)) {
            throw AuthorizationException("User has no permission to delete experiment: ${experimentId}")
        }

        if (statisticsRepository.hasAnyStatistics(experiment)) {
            throw DeleteExperimentException("Experiment with statistics cannot be deleted: ${experimentId}")
        }
    }
}