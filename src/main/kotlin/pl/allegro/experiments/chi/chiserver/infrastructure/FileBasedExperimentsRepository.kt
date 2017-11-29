package pl.allegro.experiments.chi.chiserver.infrastructure

import com.google.common.base.Strings
import com.google.common.util.concurrent.ThreadFactoryBuilder
import org.slf4j.LoggerFactory
import pl.allegro.experiments.chi.chiserver.domain.Experiment
import pl.allegro.experiments.chi.chiserver.domain.ExperimentsRepository
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.function.Consumer

class FileBasedExperimentsRepository internal constructor(jsonUrl: String, initialState: List<Experiment>, private val dataLoader: Function1<String, String>) : ExperimentsRepository {

    private val inMemoryRepository: InMemoryExperimentsRepository
    private val jsonParser: JsonParser
    private var jsonUrl: String? = null

    /**
     * @param jsonUrl nullable
     */
    constructor(jsonUrl: String, dataLoader: Function1<String, String>) : this(jsonUrl, emptyList<Experiment>(), dataLoader) {}

    init {
        this.jsonParser = JsonParser()
        this.inMemoryRepository = InMemoryExperimentsRepository(initialState)
        changeJsonUrl(jsonUrl)

        secureRefresh()
        setUpRefresher()
    }

    fun secureRefresh() {
        try {
            refresh()
        } catch (e: Exception) {
            logger.error("Error while loading experiments file.", e)
        }

    }

    private fun setUpRefresher() {
        val namedThreadFactory = ThreadFactoryBuilder().setNameFormat("chi-core-refresher-%d").build()
        val scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(namedThreadFactory)

        scheduledExecutorService.scheduleAtFixedRate({ this.secureRefresh() }, refreshRateInSeconds.toLong(), refreshRateInSeconds.toLong(), TimeUnit.SECONDS)
    }

    private fun refresh() {
        if (jsonUrl == null) {
            logger.info("Can't load experiments file - jsonUrl is null")
            return
        }

        val data = dataLoader.invoke(jsonUrl.orEmpty())
        if (Strings.isNullOrEmpty(data)) {
            logger.error("refresh failed, dataLoader has returned empty String")
            return
        }

        val freshExperiments: List<Experiment>
        try {
            freshExperiments = jsonParser.fromJSON(data).orEmpty()
        } catch (e: IllegalArgumentException) {
            logger.error("refresh failed, malformed experiments definition in JSON : {}" +
                    e.javaClass.name + " - " + e.message)
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

    fun changeJsonUrl(jsonUrl: String) {
        this.jsonUrl = jsonUrl
    }

    companion object {
        private val logger = LoggerFactory.getLogger(FileBasedExperimentsRepository::class.java)

        private val refreshRateInSeconds = 10
    }
}
