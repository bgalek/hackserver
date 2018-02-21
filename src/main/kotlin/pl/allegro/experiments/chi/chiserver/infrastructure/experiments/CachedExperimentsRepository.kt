package pl.allegro.experiments.chi.chiserver.infrastructure.experiments

import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import pl.allegro.experiments.chi.chiserver.domain.experiments.Experiment
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository
import java.util.*

class CachedExperimentsRepository(private val delegate: ExperimentsRepository): ExperimentsRepository {
    var experiments = delegate.getAll()

    companion object {
        private const val REFRESH_RATE_IN_SECONDS: Long = 1
        private val logger = LoggerFactory.getLogger(CachedExperimentsRepository::class.java)
    }


    @Scheduled(fixedDelay = REFRESH_RATE_IN_SECONDS * 1_000,
               initialDelay = REFRESH_RATE_IN_SECONDS * 1_000)
    fun secureRefresh() {
        try {
            logger.debug("loading experiments from Mongo ...")

            experiments = delegate.getAll()
        } catch (e: Exception) {
            logger.error("Error while loading all experiments from Mongo.", e)
        }
    }

    override fun getAll() : List<Experiment> = Collections.unmodifiableList(experiments)

    override fun overridable(): List<Experiment> {
        return experiments.filter { it.isOverridable() }
    }

    override fun save(experiment: Experiment) {
        delegate.save(experiment)
        secureRefresh()
    }

    override fun delete(experimentId: String) {
        delegate.delete(experimentId)
        secureRefresh()
    }

    override fun getExperiment(id: String): Experiment? = experiments.find { it.id == id }
}