package pl.allegro.experiments.chi.chiserver.application.experiments;

import pl.allegro.experiments.chi.chiserver.domain.experiments.Experiment;

public interface CrisisManagementFilter {
    Boolean filter(Experiment experiment);
}
