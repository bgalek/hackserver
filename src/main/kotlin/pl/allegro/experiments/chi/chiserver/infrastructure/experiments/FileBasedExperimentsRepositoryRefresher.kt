package pl.allegro.experiments.chi.chiserver.infrastructure.experiments

import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled

class FileBasedExperimentsRepositoryRefresher(private val experimentsRepository: FileBasedExperimentsRepository) {

    companion object {
        private const val REFRESH_RATE_IN_SECONDS: Long = 10
        private val logger = LoggerFactory.getLogger(FileBasedExperimentsRepositoryRefresher::class.java)
    }

    @Scheduled(fixedDelay = REFRESH_RATE_IN_SECONDS * 1_000,
               initialDelay = REFRESH_RATE_IN_SECONDS * 1_000)
    fun refresh() {
        //TODO usunąć
        logger.info("loading experiments from File ...")

        experimentsRepository.secureRefresh()
    }
}
