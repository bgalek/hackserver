package pl.allegro.experiments.chi.chiserver.infrastructure

import com.google.common.base.Preconditions
import com.google.common.collect.ImmutableList
import com.google.common.collect.Maps
import pl.allegro.experiments.chi.chiserver.domain.Experiment
import pl.allegro.experiments.chi.chiserver.domain.ExperimentsRepository

class InMemoryExperimentsRepository(experiments: Collection<Experiment>?) : ExperimentsRepository {
    private val experiments: MutableMap<String, Experiment>

    init {
        Preconditions.checkArgument(experiments != null)
        this.experiments = Maps.newConcurrentMap<String, Experiment>()
        experiments!!.forEach { e -> this.experiments.put(e.id, e) }
    }

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
}
