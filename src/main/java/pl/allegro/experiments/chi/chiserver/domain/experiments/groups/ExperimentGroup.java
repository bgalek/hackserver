package pl.allegro.experiments.chi.chiserver.domain.experiments.groups;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentDefinition;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ExperimentGroup {
    private final String id;
    private final String salt;
    private final List<String> experiments;

    public ExperimentGroup(
            String id,
            String salt,
            List<String> experiments) {
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

    private ExperimentGroup addExperiment(String experimentId) {
        List<String> extendedExperiments = new ArrayList<>(experiments);
        extendedExperiments.add(experimentId);
        return new ExperimentGroup(id, salt, extendedExperiments);
    }

    public static boolean enoughPercentageSpace(List<ExperimentDefinition> experiments) {
        Preconditions.checkArgument(!experiments.isEmpty());
        Preconditions.checkArgument(experiments.stream().allMatch(e -> e.getPercentage().isPresent()));

        int maxBasePercentage = experiments.stream()
                .map(e -> e.getPercentage()
                        .get())
                .max(Integer::compare)
                .get();
        int percentageSum = experiments.stream()
                .mapToInt(e -> {
                    int percentage = e.getPercentage().get();
                    int numberOfVariantsDifferentThanBase = e.getVariantNames().size() - 1;
                    return numberOfVariantsDifferentThanBase * percentage;
                }).sum();
        return maxBasePercentage + percentageSum <= 100;
    }
}