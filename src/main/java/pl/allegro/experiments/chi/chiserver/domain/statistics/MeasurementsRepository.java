package pl.allegro.experiments.chi.chiserver.domain.statistics;

public interface MeasurementsRepository {
    ExperimentMeasurements getMeasurements(String experimentId);
}