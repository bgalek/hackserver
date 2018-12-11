package pl.allegro.experiments.chi.chiserver.domain.experiments;


import java.util.Optional;

public interface CustomMetricRepository {
    Optional<CustomMetricDefinition> getCustomMetric(String cmId);

    void save(CustomMetricDefinition cm);
}
