package pl.allegro.experiments.chi.chiserver.domain.experiments

interface ExperimentsRepository {
    fun getExperiment(id: ExperimentId): Experiment?

    val all: List<Experiment>

    val active: List<Experiment>

    fun refresh()
}

interface WritableExperimentsRepository : ExperimentsRepository {
    fun save(experiment: Experiment)
}