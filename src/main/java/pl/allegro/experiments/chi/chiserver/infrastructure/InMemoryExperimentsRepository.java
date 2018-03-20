package pl.allegro.experiments.chi.chiserver.infrastructure;

import com.google.common.collect.ImmutableList;
import pl.allegro.experiments.chi.chiserver.domain.experiments.Experiment;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class InMemoryExperimentsRepository implements ExperimentsRepository {
    private final Map<String, Experiment> experiments;

    public InMemoryExperimentsRepository(Collection<Experiment> experiments) {
        this.experiments = experiments.stream()
                .collect(Collectors.toMap(Experiment::getId, e -> e));
    }

    @Override
    public Experiment getExperiment(String experimentId) {
        return experiments.get(experimentId);
    }

    @Override
    public void save(Experiment experiment) {
        experiments.put(experiment.getId(), experiment);
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
