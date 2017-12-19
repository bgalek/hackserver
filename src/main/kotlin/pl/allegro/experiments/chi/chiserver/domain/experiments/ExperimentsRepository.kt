package pl.allegro.experiments.chi.chiserver.domain.experiments

interface ExperimentsRepository {
    fun getExperiment(id: String): Experiment?

    val all: List<Experiment>

    fun refresh()
}
