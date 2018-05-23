package pl.allegro.experiments.chi.chiserver.domain.experiments.groups;

import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.stream.Collectors;

public class ExperimentGroup {
    private final String id;
    private final String nameSpace;
    private final List<String> experiments;

    public ExperimentGroup(
            String id,
            String nameSpace,
            List<String> experiments) {
        this.id = id;
        this.nameSpace = nameSpace;
        this.experiments = ImmutableList.copyOf(experiments);
    }

    public String getId() {
        return id;
    }

    public String getNameSpace() {
        return nameSpace;
    }

    public List<String> getExperiments() {
        return experiments;
    }

    public boolean contains(String experimentId) {
        return experiments.contains(experimentId);
    }

    public ExperimentGroup withRemovedExperiment(String experimentId) {
        return new ExperimentGroup(id, nameSpace, experiments.stream()
                .filter(e -> !e.equals(experimentId))
                .collect(Collectors.toList()));
    }
}