package pl.allegro.experiments.chi.chiserver.infrastructure

import com.google.common.collect.ImmutableList
import pl.allegro.experiments.chi.chiserver.domain.experiments.Experiment
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository

open class InMemoryExperimentsRepository(experiments: Collection<Experiment>) : ExperimentsRepository {
    private val experiments: MutableMap<String, Experiment> = experiments
            .associateBy { it.id }
            .toMutableMap()

    override fun getExperiment(id: String): Experiment? {
        return experiments.get(id)
    }

    override fun save(experiment: Experiment) {
        experiments.put(experiment.id, experiment)
    }

    internal fun remove(experimentId: String) {
        experiments.remove(experimentId)
    }

    override fun delete(experimentId: String) {
        remove(experimentId)
    }

    internal fun experimentIds(): Set<String> = experiments.keys

    override fun getAll() : List<Experiment> = ImmutableList.copyOf(experiments.values)

    override fun assignable(): List<Experiment> {
        return ImmutableList.copyOf(experiments.values.filter { it.isAssignable() })
    }
}
