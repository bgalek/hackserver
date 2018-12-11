package pl.allegro.experiments.chi.chiserver.infrastructure.experiments;

import org.springframework.stereotype.Repository;
import pl.allegro.experiments.chi.chiserver.domain.experiments.CustomMetricDefinition;
import pl.allegro.experiments.chi.chiserver.domain.experiments.CustomMetricRepository;

import java.util.Optional;

@Repository
public class MongoCustomMetricRepository implements CustomMetricRepository {

    private final CustomMetricDefinitionCrudRepository customMetricDefinitionCrudRepository;

    MongoCustomMetricRepository(
            CustomMetricDefinitionCrudRepository customMetricDefinitionCrudRepository) {
        this.customMetricDefinitionCrudRepository = customMetricDefinitionCrudRepository;
    }

    @Override
    public Optional<CustomMetricDefinition> getCustomMetric(String cmId) {
        return customMetricDefinitionCrudRepository.findById(cmId);
    }

    @Override
    public void save(CustomMetricDefinition cm) {
        customMetricDefinitionCrudRepository.save(cm);
    }
}
