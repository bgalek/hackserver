package pl.allegro.experiments.chi.chiserver.infrastructure

import pl.allegro.experiments.chi.chiserver.domain.Experiment
import pl.allegro.experiments.chi.chiserver.domain.ExperimentsRepository


class InMemoryExperimentsRepository(var experiments: List<Experiment>) : ExperimentsRepository {

    init {
        experiments = emptyList()
    }

    override fun getAllExperiments(): List<Experiment> {
        return experiments
    }

    fun updateExperiments(experiments: List<Experiment>) {
        this.experiments = experiments;
    }
}
