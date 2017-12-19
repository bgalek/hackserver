package pl.allegro.experiments.chi.chiserver.domain.experiments

import pl.allegro.experiments.chi.chiserver.domain.experiments.Experiment

interface ExperimentsRepository {
    fun getExperiment(id: String): Experiment?

    val all: List<Experiment>

    fun refresh()
}
