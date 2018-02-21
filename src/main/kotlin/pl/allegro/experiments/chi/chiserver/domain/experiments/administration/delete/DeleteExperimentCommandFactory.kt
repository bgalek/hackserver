package pl.allegro.experiments.chi.chiserver.domain.experiments.administration.delete

import org.springframework.stereotype.Component
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.PermissionsAwareExperimentGetter
import pl.allegro.experiments.chi.chiserver.domain.statistics.StatisticsRepository

@Component
class DeleteExperimentCommandFactory(
        val experimentsRepository: ExperimentsRepository,
        val permissionsAwareExperimentGetter: PermissionsAwareExperimentGetter,
        val statisticsRepository: StatisticsRepository) {

    fun deleteExperimentCommand(experimentId: String): DeleteExperimentCommand {
        return DeleteExperimentCommand(
                experimentsRepository,
                permissionsAwareExperimentGetter,
                experimentId,
                statisticsRepository)
    }
}