package pl.allegro.experiments.chi.chiserver.domain

interface ExperimentsRepository {
    fun getExperiment(id: String): Experiment?

    val all: List<Experiment>

    fun refresh()

    fun reportingEnabled(experimentId: String): Boolean {
        val experiment = getExperiment(experimentId)
        return experiment != null && experiment.reportingEnabled
    }
}
