package pl.allegro.experiments.chi.chiserver.infrastructure.experiments

import com.google.common.base.Strings
import org.slf4j.LoggerFactory
import pl.allegro.experiments.chi.chiserver.domain.experiments.Experiment
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository
import pl.allegro.experiments.chi.chiserver.infrastructure.InMemoryExperimentsRepository
import pl.allegro.experiments.chi.chiserver.infrastructure.JsonParser

class FileBasedExperimentsRepository(jsonUrl: String, initialState: List<Experiment>, private val dataLoader: (String) -> String) : ExperimentsRepository {

    private val inMemoryRepository: InMemoryExperimentsRepository = InMemoryExperimentsRepository(initialState)
    private val jsonParser: JsonParser = JsonParser()
    private var jsonUrl: String = jsonUrl
        set(jsonUrl) { field = jsonUrl }

    constructor(jsonUrl: String, dataLoader: (String) -> String) : this(jsonUrl, emptyList<Experiment>(), dataLoader)

    companion object {
        private val logger = LoggerFactory.getLogger(FileBasedExperimentsRepository::class.java)
    }

    init {
        refresh()
    }

    override fun refresh() {
        try {
            rawRefresh()
        } catch (e: Exception) {
            logger.error("Error while loading experiments file.", e)
        }
    }

    private fun rawRefresh() {
        val data = dataLoader.invoke(jsonUrl)

        if (Strings.isNullOrEmpty(data)) {
            logger.error("refresh failed, dataLoader has returned empty String")
            return
        }

        val freshExperiments: List<Experiment>
        try {
            freshExperiments = jsonParser.fromJSON(data).orEmpty()
        } catch (e: IllegalArgumentException) {
            logger.error("refresh failed, malformed experiments definition in JSON: " + e.message, e)
            return
        }

        val currentExperimentsIds = inMemoryRepository.experimentIds()
        val freshExperimentsIds = freshExperiments.map { it.id }.toSet()

        val removedExperiments = currentExperimentsIds.minus(freshExperimentsIds)

        freshExperiments.forEach { inMemoryRepository.save(it) }
        removedExperiments.forEach { inMemoryRepository.remove(it) }

        logger.debug("{} experiment(s) successfully loaded", freshExperiments.size)
    }

    override fun getExperiment(id: String): Experiment? {
        return inMemoryRepository.getExperiment(id)
    }

    override val all: List<Experiment>
        get() = inMemoryRepository.all
}
