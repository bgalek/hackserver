package pl.allegro.experiments.chi.chiserver.infrastructure

import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import pl.allegro.experiments.chi.chiserver.logger

@Service
class InMemoryExperimentsRepositoryRefresher(val fileBasedExperimentsRepository: FileBasedExperimentsRepository,
                                             val inMemoryExperimentsRepository: InMemoryExperimentsRepository) {

    companion object {
        private val logger by logger()
    }

    @Scheduled(fixedDelay = 10000)
    fun refresh() {

        logger.debug("Refreshing experiments")

        try {
            val allExperiments = fileBasedExperimentsRepository.getAllExperiments()

            logger.debug("Fetched ${allExperiments.size} experiments")

            inMemoryExperimentsRepository.updateExperiments(allExperiments)

        } catch (e: Exception) {
            logger.error("Problem with refreshing experiments", e);
        }
    }
}