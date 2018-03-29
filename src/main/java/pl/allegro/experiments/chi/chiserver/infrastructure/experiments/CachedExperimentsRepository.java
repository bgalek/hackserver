package pl.allegro.experiments.chi.chiserver.infrastructure.experiments;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import pl.allegro.experiments.chi.chiserver.domain.experiments.Experiment;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentDefinition;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CachedExperimentsRepository implements ExperimentsRepository {
    private List<Experiment> experiments;
    private final ExperimentsRepository delegate;
    private static final long REFRESH_RATE_IN_SECONDS = 1;
    private static final Logger logger = LoggerFactory.getLogger(CachedExperimentsRepository.class);

    public CachedExperimentsRepository(ExperimentsRepository delegate) {
        this.delegate = delegate;
        this.experiments = delegate.getAll();
    }

    @Scheduled(fixedDelay = REFRESH_RATE_IN_SECONDS * 1_000,
            initialDelay = REFRESH_RATE_IN_SECONDS * 1_000)
    public void secureRefresh() {
        try {
            logger.debug("loading experiments from Mongo ...");

            experiments = delegate.getAll();
        } catch (Exception e) {
            logger.error("Error while loading all experiments from Mongo.", e);
        }
    }

    @Override
    public List<Experiment> getAll() {
        return Collections.unmodifiableList(experiments);
    }

    @Override
    public List<Experiment> assignable() {
        return experiments.stream()
                .filter(Experiment::isAssignable)
                .collect(Collectors.toList());
    }

    @Override
    public void save(ExperimentDefinition experimentDefinition) {
        delegate.save(experimentDefinition);
        secureRefresh();
    }

    @Override
    public void delete(String experimentId) {
        delegate.delete(experimentId);
        secureRefresh();
    }

    @Override
    public Optional<Experiment> getExperiment(String experimentId) {
        return Optional.ofNullable(experiments.stream()
                .filter(it -> it.getId().equals(experimentId))
                .findFirst()
                .orElse(null));
    }
}
