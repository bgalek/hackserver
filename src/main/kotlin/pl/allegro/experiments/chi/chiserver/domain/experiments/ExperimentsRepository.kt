package pl.allegro.experiments.chi.chiserver.domain.experiments

interface ExperimentsRepository {
    fun getExperiment(id: ExperimentId): Experiment?

    val all: List<Experiment>

    val overridable: List<Experiment>

    fun save(experiment: Experiment)
}
