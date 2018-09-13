package pl.allegro.experiments.chi.chiserver.infrastructure;

import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentDefinition;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentVariant;

import java.util.List;
import java.util.stream.Collectors;

public class ClientExperimentInfo {
    private final String name;
    private final List<String> variants;

    public ClientExperimentInfo(ExperimentDefinition experiment) {
        name = experiment.getId();
        variants = experiment.prepareExperimentVariants()
                .stream()
                .map(ExperimentVariant::getName)
                .collect(Collectors.toList());
    }

    ClientExperimentInfo(String id, List<String> variants) {
        this.name = id;
        this.variants = variants;
    }

    public String getName() {
        return name;
    }

    public List<String> getVariants() {
        return variants;
    }
}
