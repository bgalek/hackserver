package pl.allegro.experiments.chi.chiserver.infrastructure.experiments;

import org.springframework.data.repository.CrudRepository;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentDefinition;

public interface ExperimentDefinitionCrudRepository extends CrudRepository<ExperimentDefinition, String> {
}
