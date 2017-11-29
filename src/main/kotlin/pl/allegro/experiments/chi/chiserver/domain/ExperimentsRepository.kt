package pl.allegro.experiments.chi.chiserver.domain

interface ExperimentsRepository {
    fun getExperiment(id: String): Experiment?

    val all: List<Experiment>
}
