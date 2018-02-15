package pl.allegro.experiments.chi.chiserver.infrastructure.experiments

import com.github.salomonbrys.kotson.fromJson
import org.slf4j.LoggerFactory
import pl.allegro.experiments.chi.chiserver.domain.experiments.Experiment
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentId
import pl.allegro.experiments.chi.chiserver.domain.experiments.ReadOnlyExperimentsRepository
import pl.allegro.experiments.chi.chiserver.infrastructure.InMemoryExperimentsRepository
import pl.allegro.experiments.chi.chiserver.infrastructure.JsonConverter
import javax.naming.OperationNotSupportedException

open class FileBasedExperimentsRepository(jsonUrl: String,
                                     private val dataLoader: (String) -> String,
                                     private val jsonConverter: JsonConverter,
                                     initialState: List<Experiment> = emptyList()) : ReadOnlyExperimentsRepository {

    private val inMemoryRepository: InMemoryExperimentsRepository = InMemoryExperimentsRepository(initialState)
    private var jsonUrl: String = jsonUrl

    companion object {
        private val logger = LoggerFactory.getLogger(FileBasedExperimentsRepository::class.java)
    }

    init {
        secureRefresh()
    }

    fun secureRefresh() {
        try {
            refresh()
        } catch (e: Exception) {
            logger.error("Error while loading experiments file.", e)
        }
    }

    private fun refresh() {
        val data = dataLoader.invoke(jsonUrl)

        if (data.isBlank()) {
            logger.error("refresh failed, dataLoader has returned empty String")
            return
        }

        val freshExperiments: List<Experiment>
        try {
            freshExperiments = jsonConverter.fromJson(data)
        } catch (e: IllegalArgumentException) {
            logger.error("refresh failed, malformed experiments definition in JSON: ${e.message}", e)
            return
        }

        val currentExperimentsIds = inMemoryRepository.experimentIds()
        val freshExperimentsIds = freshExperiments.map { it.id }.toSet()

        val removedExperiments = currentExperimentsIds.minus(freshExperimentsIds)

        freshExperiments.forEach { inMemoryRepository.save(it) }
        removedExperiments.forEach { inMemoryRepository.remove(it) }

        logger.debug("{} experiment(s) successfully loaded", freshExperiments.size)
    }

    override fun getExperiment(id: ExperimentId): Experiment? = inMemoryRepository.getExperiment(id)

    override fun getAll() : List<Experiment> = inMemoryRepository.getAll()

    override val overridable: List<Experiment>
        get() = inMemoryRepository.overridable
}
