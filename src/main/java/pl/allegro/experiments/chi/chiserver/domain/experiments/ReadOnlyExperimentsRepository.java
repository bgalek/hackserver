package pl.allegro.experiments.chi.chiserver.domain.experiments;

import java.util.List;
import java.util.Optional;

public interface ReadOnlyExperimentsRepository {
    Optional<Experiment> getExperiment(String experimentId);

    List<Experiment> getAll();

    List<Experiment> assignable();
}
