package pl.allegro.experiments.chi.chiserver.domain.experiments.groups;

import java.util.List;
import java.util.Optional;

public interface ExperimentGroupRepository {
    void save(ExperimentGroup experimentGroup);

    Optional<ExperimentGroup> get(String id);

    boolean exists(String id);

    boolean experimentInGroup(String experimentId);

    List<ExperimentGroup> all();

    Optional<ExperimentGroup> getExperimentGroup(String experimentId);
}
