package pl.allegro.experiments.chi.chiserver.domain.experiments

interface ReadOnlyExperimentsRepository {
    fun getExperiment(id: ExperimentId): Experiment?

    fun getAll() : List<Experiment>

    val overridable: List<Experiment>
}