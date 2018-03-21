package pl.allegro.experiments.chi.chiserver.domain.experiments;

import pl.allegro.experiments.chi.chiserver.infrastructure.experiments.ExperimentOrigin;

public interface ExperimentsRepository extends ReadOnlyExperimentsRepository {
    void delete(String experimentId);

    void save(Experiment experiment);

    default ExperimentOrigin getOrigin(String experimentId) {
        return ExperimentOrigin.UNDEFINED;
    }
}