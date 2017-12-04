package pl.allegro.experiments.chi.chiserver.infrastructure

import com.google.common.base.Strings
import org.slf4j.LoggerFactory
import pl.allegro.experiments.chi.chiserver.domain.Experiment
import pl.allegro.experiments.chi.chiserver.domain.ExperimentsRepository
import java.util.function.Consumer

class FileBasedExperimentsRepository(private var jsonUrl: String, initialState: List<Experiment>, private val dataLoader: Function1<String, String>) : ExperimentsRepository {

    private val inMemoryRepository: InMemoryExperimentsRepository = InMemoryExperimentsRepository(initialState)
    private val jsonParser: JsonParser = JsonParser()

    constructor(jsonUrl: String, dataLoader: Function1<String, String>) : this(jsonUrl, emptyList<Experiment>(), dataLoader)

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

    fun changeJsonUrl(jsonUrl: String) {
        this.jsonUrl = jsonUrl
    }

    private fun refresh() {
        val data = dataLoader.invoke(jsonUrl)
        if (Strings.isNullOrEmpty(data)) {
            logger.error("refresh failed, dataLoader has returned empty String")
            return
        }

        val freshExperiments: List<Experiment>
        try {
            freshExperiments = jsonParser.fromJSON(data).orEmpty()
        } catch (e: IllegalArgumentException) {
            logger.error("refresh failed, malformed experiments definition in JSON : {}" + e.javaClass.name + " - " + e.message)
            return
        }

        val currentExperimentsIds: Set<String> = inMemoryRepository.experimentIds()
        val freshExperimentsIds : Set<String> = freshExperiments.map { it -> it.id }.toSet()

        val removedExperiments: Set<String> = currentExperimentsIds.minus(freshExperimentsIds)

        freshExperiments.forEach(Consumer<Experiment> { inMemoryRepository.save(it) })
        removedExperiments.forEach(Consumer<String> { inMemoryRepository.remove(it) })

        logger.debug("{} experiment(s) successfully loaded", freshExperiments.size)
    }

    override fun getExperiment(id: String): Experiment? {
        return inMemoryRepository.getExperiment(id)
    }

    override val all: List<Experiment>
        get() = inMemoryRepository.all
}
