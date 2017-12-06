package pl.allegro.experiments.chi.chiserver.infrastructure

import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import pl.allegro.experiments.chi.chiserver.domain.ExperimentsRepository

class ExperimentRepositoryRefresher (private val repository: ExperimentsRepository) {

    companion object {
        private val logger = LoggerFactory.getLogger(ExperimentRepositoryRefresher::class.java)
        private const val REFRESH_RATE_IN_SECONDS: Long = 10
    }

    @Scheduled(fixedDelay = REFRESH_RATE_IN_SECONDS * 1_000, initialDelay = REFRESH_RATE_IN_SECONDS * 1_000)
    fun refresh() {
        repository.refresh()
    }

}