package pl.allegro.experiments.chi.chiserver.domain.experiments;

import java.util.List;
import java.util.Optional;

public interface ExperimentTagRepository {
    void save(ExperimentTag experimentTag);

    Optional<ExperimentTag> get(String experimentTagId);

    List<ExperimentTag> all();

    boolean tagsExist(List<String> experimentTagIds);
}
