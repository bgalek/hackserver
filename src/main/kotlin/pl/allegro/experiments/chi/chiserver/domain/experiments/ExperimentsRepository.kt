package pl.allegro.experiments.chi.chiserver.domain.experiments

interface ReadOnlyExperimentsRepository {
    fun getExperiment(id: ExperimentId): Experiment?

    fun getAll() : List<Experiment>

    val overridable: List<Experiment>
}

interface ExperimentsRepository : ReadOnlyExperimentsRepository {

    fun delete(experimentId: ExperimentId)

    fun save(experiment: Experiment)

    /** to be removed with StashRepository */
    fun getOrigin(experimentId: String) : String = "undefined"
}
