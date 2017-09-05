package pl.allegro.experiments.chi.chiserver.domain


interface ExperimentsRepository {
    fun getAllExperiments(): List<Experiment>
}
