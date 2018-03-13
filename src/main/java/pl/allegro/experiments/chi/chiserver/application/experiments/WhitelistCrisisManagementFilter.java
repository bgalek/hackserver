package pl.allegro.experiments.chi.chiserver.application.experiments;

import pl.allegro.experiments.chi.chiserver.domain.experiments.Experiment;

import java.util.List;

public class WhitelistCrisisManagementFilter implements CrisisManagementFilter {
    private final List<String> whitelist;

    public WhitelistCrisisManagementFilter(List<String> whitelist) {
        this.whitelist = whitelist;
    }

    @Override
    public Boolean filter(Experiment experiment) {
        return whitelist.contains(experiment.getId());
    }
}
