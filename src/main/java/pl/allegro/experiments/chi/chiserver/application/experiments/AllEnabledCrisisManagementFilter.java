package pl.allegro.experiments.chi.chiserver.application.experiments;

import pl.allegro.experiments.chi.chiserver.domain.experiments.Experiment;

public class AllEnabledCrisisManagementFilter implements CrisisManagementFilter {
    @Override
    public Boolean filter(Experiment experiment) {
        return true;
    }
}
