package pl.allegro.experiments.chi.chiserver.domain.experiments.administration.delete

import org.springframework.stereotype.Component
import pl.allegro.experiments.chi.chiserver.domain.UserProvider
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentId
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository
import pl.allegro.experiments.chi.chiserver.domain.statistics.StatisticsRepository

@Component
class DeleteExperimentCommandFactory(val experimentsRepository: ExperimentsRepository,
                                     val userProvider: UserProvider,
                                     val statisticsRepository: StatisticsRepository) {

    fun deleteExperimentCommand(experimentId: ExperimentId): DeleteExperimentCommand {
        return DeleteExperimentCommand(experimentsRepository, userProvider, experimentId, statisticsRepository)
    }
}