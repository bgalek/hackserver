package pl.allegro.experiments.chi.chiserver.infrastructure.experiments;

import com.google.common.collect.ImmutableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import pl.allegro.experiments.chi.chiserver.domain.experiments.groups.ExperimentGroup;
import pl.allegro.experiments.chi.chiserver.domain.experiments.groups.ExperimentGroupRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CachedExperimentGroupRepository implements ExperimentGroupRepository {
    private List<ExperimentGroup> experimentGroups;
    private Map<String, ExperimentGroup> experimentGroupMap;
    private final ExperimentGroupRepository delegate;
    private static final long REFRESH_RATE_IN_SECONDS = 1;
    private static final Logger logger = LoggerFactory.getLogger(CachedExperimentGroupRepository.class);

    CachedExperimentGroupRepository(ExperimentGroupRepository delegate) {
        this.delegate = delegate;
        refresh();
    }

    @Scheduled(fixedDelay = REFRESH_RATE_IN_SECONDS * 1_000,
            initialDelay = REFRESH_RATE_IN_SECONDS * 1_000)
    public void secureRefresh() {
        try {
            logger.debug("loading experiment groups from Mongo ...");
            refresh();
        } catch (Exception e) {
            logger.error("Error while loading all experiment groups from Mongo.", e);
        }
    }

    private void refresh() {
        experimentGroups = delegate.all();
        experimentGroupMap = buildExperimentGroupMap(experimentGroups);
    }

    private Map<String, ExperimentGroup> buildExperimentGroupMap(List<ExperimentGroup> experimentGroups) {
        Map<String, ExperimentGroup> result = new HashMap<>();
        for (ExperimentGroup experimentGroup: experimentGroups) {
            for (String experimentId: experimentGroup.getExperiments()) {
                result.put(experimentId, experimentGroup);
            }
        }
        return result;
    }

    @Override
    public void save(ExperimentGroup experimentGroup) {
        delegate.save(experimentGroup);
        secureRefresh();
    }

    @Override
    public Optional<ExperimentGroup> get(String id) {
        return experimentGroups.stream()
                .filter(it -> it.getId().equals(id))
                .findFirst();
    }

    @Override
    public boolean exists(String id) {
        return get(id).isPresent();
    }

    @Override
    public boolean experimentInGroup(String experimentId) {
        return getExperimentGroup(experimentId).isPresent();
    }

    @Override
    public List<ExperimentGroup> all() {
        return ImmutableList.copyOf(experimentGroups);
    }

    @Override
    public Optional<ExperimentGroup> getExperimentGroup(String experimentId) {
        return Optional.ofNullable(experimentGroupMap.get(experimentId));
    }
}
