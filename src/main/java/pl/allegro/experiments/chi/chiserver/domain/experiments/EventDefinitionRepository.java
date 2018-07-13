package pl.allegro.experiments.chi.chiserver.domain.experiments;

import java.util.List;

public interface EventDefinitionRepository {
    void saveExperimentsEventDefinitions(List<ExperimentDefinition> experimentDefinition);
}
