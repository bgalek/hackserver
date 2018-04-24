package pl.allegro.experiments.chi.chiserver.domain.experiments;

public interface MeasurementsRepository {
    ExperimentMeasurements getMeasurements(String experimentId);
}