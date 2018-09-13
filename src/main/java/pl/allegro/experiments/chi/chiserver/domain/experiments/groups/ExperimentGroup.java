package pl.allegro.experiments.chi.chiserver.domain.experiments.groups;

import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ExperimentGroup implements Comparable<ExperimentGroup> {
    private final String id;
    private final String salt;
    private final List<String> experiments;

    public ExperimentGroup(String id, String salt, List<String> experiments) {
        this.id = id;
        this.salt = salt;
        this.experiments = ImmutableList.copyOf(experiments);
    }

    public String getId() {
        return id;
    }

    public String getSalt() {
        return salt;
    }

    public List<String> getExperiments() {
        return experiments;
    }

    public boolean contains(String experimentId) {
        return experiments.contains(experimentId);
    }

    public ExperimentGroup removeExperiment(String experimentId) {
        return new ExperimentGroup(id, salt, experiments.stream()
                .filter(e -> !e.equals(experimentId))
                .collect(Collectors.toList()));
    }

    public ExperimentGroup moveExperimentToTail(String experimentId) {
        return removeExperiment(experimentId).addExperiment(experimentId);
    }

    public ExperimentGroup addExperiment(String experimentId) {
        List<String> extendedExperiments = new ArrayList<>(experiments);
        extendedExperiments.add(experimentId);
        return new ExperimentGroup(id, salt, extendedExperiments);
    }

    @Override
    public int compareTo(ExperimentGroup o) {
        return this.id.compareTo(o.id);
    }
}