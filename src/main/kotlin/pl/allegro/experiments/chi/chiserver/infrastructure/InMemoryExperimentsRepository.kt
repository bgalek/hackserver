package pl.allegro.experiments.chi.chiserver.infrastructure

import com.google.common.collect.ImmutableList
import pl.allegro.experiments.chi.chiserver.domain.Experiment
import pl.allegro.experiments.chi.chiserver.domain.ExperimentsRepository

class InMemoryExperimentsRepository(experiments: Collection<Experiment>) : ExperimentsRepository {

    private val experiments: MutableMap<String, Experiment> = experiments
            .associateBy { it.id }
            .toMutableMap()

    override fun getExperiment(id: String): Experiment? {
        return experiments.get(id)
    }

    internal fun remove(experimentId: String) {
        experiments.remove(experimentId)
    }

    internal fun save(experiment: Experiment) {
        experiments.put(experiment.id, experiment)
    }

    internal fun experimentIds(): Set<String> {
        return experiments.keys
    }

    override val all: List<Experiment>
        get() = ImmutableList.copyOf(experiments.values)

    override fun refresh() {
        // Nothing to do
    }
}
