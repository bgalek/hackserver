package pl.allegro.experiments.chi.chiserver.infrastructure.experiments

import org.slf4j.LoggerFactory
import pl.allegro.experiments.chi.chiserver.domain.experiments.Experiment
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentId
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository
import pl.allegro.experiments.chi.chiserver.domain.experiments.WritableExperimentsRepository

class ExperimentsMultiRepository(private val repositories: List<ExperimentsRepository>) :
    WritableExperimentsRepository {
    private var experiments = emptyMap<ExperimentId, Experiment>()
    private val reposCache = repositories.mapTo(mutableListOf()) { emptyList<Experiment>() }

    companion object {
        private val logger = LoggerFactory.getLogger(ExperimentsMultiRepository::class.java)
    }

    init {
        refresh()
    }

    override fun getExperiment(id: ExperimentId): Experiment? = experiments[id]

    override fun save(experiment: Experiment) {
        with(repositories.filterIsInstance(WritableExperimentsRepository::class.java)) {
            check(isNotEmpty()) { "No writable experiments repository configured" }
            forEach { it.save(experiment) }
        }
    }

    override val all: List<Experiment>
        get() = experiments.entries.map { it.value }

    override val assignable: List<Experiment>
        get() = all.filter { it.isAssignable() }

    override fun refresh() {
        repositories.forEachIndexed { index, repo ->
            try {
                repo.refresh()
                reposCache[index] = repo.all
            } catch (e: Exception) {
                logger.error("Error while refreshing experiments in {}.", repo.javaClass, e)
            }
        }
        experiments =
            reposCache.fold(emptyMap(), { acc, experiments ->
                acc + experiments.associateBy({ it.id }, { it })
            })
    }
}