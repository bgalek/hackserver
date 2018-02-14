package pl.allegro.experiments.chi.chiserver.infrastructure.experiments

import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import pl.allegro.experiments.chi.chiserver.domain.experiments.Experiment
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentId
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository

class CachedMongoExperimentsRepository(private val delegate: MongoExperimentsRepository): ExperimentsRepository {
    var experiments = delegate.all

    companion object {
        private const val REFRESH_RATE_IN_SECONDS: Long = 1
        private val logger = LoggerFactory.getLogger(CachedMongoExperimentsRepository::class.java)
    }


    @Scheduled(fixedDelay = REFRESH_RATE_IN_SECONDS * 1_000,
               initialDelay = REFRESH_RATE_IN_SECONDS * 1_000)
    fun secureRefresh() {
        try {
            //TODO usunąć
            logger.info("loading experiments from Mongo ...")

            experiments = delegate.all
        } catch (e: Exception) {
            logger.error("Error while loading all experiments from Mongo.", e)
        }
    }

    override val all: List<Experiment>
        get() = experiments

    override val overridable: List<Experiment>
        get() = experiments.filter { it.isOverridable() }

    override fun save(experiment: Experiment) {
        delegate.save(experiment)
        secureRefresh()
    }

    override fun delete(experimentId: ExperimentId) {
        delegate.delete(experimentId)
        secureRefresh()
    }

    override fun getExperiment(id: ExperimentId): Experiment? {
        return experiments.find { it.id == id }
    }
}