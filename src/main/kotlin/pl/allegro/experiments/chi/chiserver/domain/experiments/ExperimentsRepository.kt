package pl.allegro.experiments.chi.chiserver.domain.experiments

interface ExperimentsRepository {
    fun getExperiment(id: ExperimentId): Experiment?

    val all: List<Experiment>

    val overridable: List<Experiment>

    fun delete(experimentId: ExperimentId)

    fun save(experiment: Experiment)

    @Deprecated("Should be considered again when removing temporary DoubleExperiments repo. Now it simplifies code a bit")
    fun refresh()
}
