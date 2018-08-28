package pl.allegro.experiments.chi.chiserver.application.experiments;

import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentDefinition;

public class AllEnabledCrisisManagementFilter implements CrisisManagementFilter {
    @Override
    public Boolean filter(ExperimentDefinition experiment) {
        return true;
    }
}
