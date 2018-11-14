package pl.allegro.experiments.chi.chiserver.domain.experiments;

import java.util.Optional;

public interface ExperimentTagRepository {
    void save(ExperimentTag experimentTag);

    Optional<ExperimentTag> get(String experimentTagId);
}
