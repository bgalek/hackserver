package pl.allegro.experiments.chi.chiserver.domain.experiments

interface MeasurementsRepository {
    fun withMeasurements(experiment: Experiment): Experiment

    fun withMeasurements(experiments: List<Experiment>): List<Experiment>
}
