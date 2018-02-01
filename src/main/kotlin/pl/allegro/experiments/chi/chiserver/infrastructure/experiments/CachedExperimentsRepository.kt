package pl.allegro.experiments.chi.chiserver.infrastructure.experiments

import pl.allegro.experiments.chi.chiserver.domain.experiments.Experiment
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentId
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository

class CachedExperimentsRepository(private val delegate: ExperimentsRepository): ExperimentsRepository {
    var experiments = delegate.all

    override val all: List<Experiment>
        get() = experiments

    override val overridable: List<Experiment>
        get() = experiments.filter { it.isOverridable() }

    override fun save(experiment: Experiment) {
        delegate.save(experiment)
        refresh()
    }

    override fun getExperiment(id: ExperimentId): Experiment? {
        return experiments.find { it.id == id }
    }

    fun refresh() {
        experiments = delegate.all
    }
}
