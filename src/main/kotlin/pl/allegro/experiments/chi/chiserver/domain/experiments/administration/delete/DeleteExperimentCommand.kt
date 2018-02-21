package pl.allegro.experiments.chi.chiserver.domain.experiments.administration.delete

import pl.allegro.experiments.chi.chiserver.domain.experiments.Experiment
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.PermissionsAwareExperimentGetter
import pl.allegro.experiments.chi.chiserver.domain.statistics.StatisticsRepository


class DeleteExperimentCommand(private val experimentsRepository: ExperimentsRepository,
                              private val permissionsAwareExperimentGetter: PermissionsAwareExperimentGetter,
                              private val experimentId: String,
                              private val statisticsRepository: StatisticsRepository) {

    fun execute() {
        val experiment = permissionsAwareExperimentGetter.getExperimentOrException(experimentId)
        validate(experiment)
        experimentsRepository.delete(experimentId)
    }

    private fun validate(experiment: Experiment) {
        if (statisticsRepository.hasAnyStatistics(experiment)) {
            throw DeleteExperimentException("Experiment with statistics cannot be deleted: ${experimentId}")
        }
    }
}