package pl.allegro.experiments.chi.chiserver.domain.experiments;

import java.util.List;

public interface MeasurementsRepository {
    Experiment withMeasurements(Experiment experiment);

    List<Experiment> withMeasurements(List<Experiment> experiments);
}