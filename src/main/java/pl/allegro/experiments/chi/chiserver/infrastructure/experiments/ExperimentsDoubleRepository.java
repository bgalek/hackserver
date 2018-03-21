package pl.allegro.experiments.chi.chiserver.infrastructure.experiments;

import pl.allegro.experiments.chi.chiserver.domain.experiments.Experiment;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ReadOnlyExperimentsRepository;

import java.util.*;
import java.util.stream.Collectors;

public class ExperimentsDoubleRepository implements ExperimentsRepository {
    private final ReadOnlyExperimentsRepository readOnlyExperimentsRepository;
    private final ExperimentsRepository mongoExperimentsRepository;

    public ExperimentsDoubleRepository(
            ReadOnlyExperimentsRepository readOnlyExperimentsRepository,
            ExperimentsRepository mongoExperimentsRepository) {
        this.readOnlyExperimentsRepository = readOnlyExperimentsRepository;
        this.mongoExperimentsRepository = mongoExperimentsRepository;
    }

    @Override
    public Experiment getExperiment(String id) {
        return Optional.ofNullable(readOnlyExperimentsRepository.getExperiment(id))
                .orElse(mongoExperimentsRepository.getExperiment(id));
    }

    @Override
    public void save(Experiment experiment) {
        if (getOrigin(experiment.getId()).equals("stash")) {
            throw new IllegalArgumentException("experiment " + experiment.getId() + " is from stash");
        }
        mongoExperimentsRepository.save(experiment);
    }

    @Override
    public void delete(String experimentId) {
        mongoExperimentsRepository.delete(experimentId);
    }

    @Override
    public List<Experiment> getAll() {
        List<Experiment> readOnlyExperiments = readOnlyExperimentsRepository.getAll();
        Set<String> readOnlyKeys = readOnlyExperiments.stream()
                .map(Experiment::getId)
                .collect(Collectors.toSet());
        List<Experiment> merged = new ArrayList<>(readOnlyExperiments);
        mongoExperimentsRepository.getAll().forEach(e -> {
            if (!readOnlyKeys.contains(e.getId())) {
                merged.add(e);
            }
        });
        return merged;
    }

    @Override
    public ExperimentOrigin getOrigin(String experimentId) {
        if (readOnlyExperimentsRepository.getExperiment(experimentId) != null) {
            return ExperimentOrigin.STASH;
        }
        if (mongoExperimentsRepository.getExperiment(experimentId) != null) {
            return ExperimentOrigin.MONGO;
        }
        return ExperimentsRepository.super.getOrigin(experimentId);
    }

    @Override
    public List<Experiment> assignable() {
        return getAll().stream().filter(Experiment::isAssignable).collect(Collectors.toList());
    }
}
