package pl.allegro.experiments.chi.chiserver.infrastructure

import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled

class ExperimentRepositoryRefresher (val repository: FileBasedExperimentsRepository) {

    companion object {
        private val logger = LoggerFactory.getLogger(ExperimentRepositoryRefresher::class.java)
        private const val refreshRateInSeconds : Long = 10
    }

    @Scheduled(fixedDelay = refreshRateInSeconds * 1_000, initialDelay = refreshRateInSeconds * 1_000)
    fun refresh() {
        repository.secureRefresh()
    }

}
