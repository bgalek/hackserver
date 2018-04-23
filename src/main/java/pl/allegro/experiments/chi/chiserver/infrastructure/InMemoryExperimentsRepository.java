package pl.allegro.experiments.chi.chiserver.infrastructure;

import com.google.common.collect.ImmutableList;
import pl.allegro.experiments.chi.chiserver.domain.experiments.Experiment;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentDefinition;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class InMemoryExperimentsRepository implements ExperimentsRepository {
    private final Map<String, Experiment> experiments = new ConcurrentHashMap<>();

    public InMemoryExperimentsRepository(Collection<Experiment> experiments) {
        experiments.forEach(e -> this.experiments.put(e.getId(),e));
    }

    @Override
    public Optional<Experiment> getExperiment(String experimentId) {
        return Optional.ofNullable(experiments.get(experimentId));
    }

    public void save(Experiment experiment) {
        experiments.put(experiment.getId(), experiment);
    }

    @Override
    public void save(ExperimentDefinition experimentDefinition) {
        experiments.put(experimentDefinition.getId(), experimentDefinition.toExperiment());
    }

    @Override
    public void delete(String experimentId) {
        remove(experimentId);
    }

    @Override
    public List<Experiment> getAll() {
        return ImmutableList.copyOf(experiments.values());
    }

    @Override
    public List<Experiment> assignable() {
        return ImmutableList.copyOf(experiments.values().stream()
                .filter(Experiment::isAssignable)
                .collect(Collectors.toList()));
    }

    public Set<String> experimentIds() {
        return experiments.keySet();
    }

    public void remove(String experimentId) {
        experiments.remove(experimentId);
    }
}
