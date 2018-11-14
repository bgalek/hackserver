package pl.allegro.experiments.chi.chiserver.infrastructure.experiments;

import org.springframework.data.repository.CrudRepository;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentTag;

import java.util.Optional;

public interface ExperimentTagCrudRepository extends CrudRepository<ExperimentTag, String> {

    ExperimentTag save(ExperimentTag entity);

    Optional<ExperimentTag> findById(String experimentTagId);
}
