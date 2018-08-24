package pl.allegro.experiments.chi.chiserver.domain.experiments;

import java.util.List;
import java.util.Optional;

public interface ExperimentsRepository {
    Optional<ExperimentDefinition> getExperiment(String experimentId);

    List<ExperimentDefinition> getAll();

    List<ExperimentDefinition> assignable();

    void delete(String experimentId);

    void save(ExperimentDefinition experiment);
}