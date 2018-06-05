package pl.allegro.experiments.chi.chiserver.domain.experiments.groups;

import java.util.List;
import java.util.Optional;

public interface ExperimentGroupRepository {
    void save(ExperimentGroup experimentGroup);

    Optional<ExperimentGroup> findById(String id);

    boolean exists(String id);

    boolean experimentInGroup(String experimentId);

    List<ExperimentGroup> findAll();

    Optional<ExperimentGroup> findByExperimentId(String experimentId);
}
