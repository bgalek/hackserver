package pl.allegro.experiments.chi.chiserver.application.experiments;

import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentDefinition;

public interface CrisisManagementFilter {
    Boolean filter(ExperimentDefinition experiment);
}
