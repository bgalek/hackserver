package pl.allegro.experiments.chi.chiserver.domain.experiments;

import java.util.List;

public interface ReadOnlyExperimentsRepository {
    Experiment getExperiment(String experimentId);

    List<Experiment> getAll();

    List<Experiment> assignable();
}
