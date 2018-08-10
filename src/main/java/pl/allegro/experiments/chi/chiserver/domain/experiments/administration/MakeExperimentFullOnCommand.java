package pl.allegro.experiments.chi.chiserver.domain.experiments.administration;

import pl.allegro.experiments.chi.chiserver.domain.experiments.Experiment;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentDefinition;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentVariant;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository;
import pl.allegro.experiments.chi.chiserver.domain.experiments.groups.ExperimentGroupRepository;

import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

public class MakeExperimentFullOnCommand {
    private final String experimentId;
    private final MakeExperimentFullOnProperties properties;
    private final ExperimentsRepository experimentsRepository;
    private final ExperimentGroupRepository experimentGroupRepository;
    private final PermissionsAwareExperimentRepository permissionsAwareExperimentRepository;

    MakeExperimentFullOnCommand(
            String experimentId,
            MakeExperimentFullOnProperties properties,
            ExperimentsRepository experimentsRepository,
            ExperimentGroupRepository experimentGroupRepository,
            PermissionsAwareExperimentRepository permissionsAwareExperimentRepository) {
        this.experimentId = experimentId;
        this.properties = properties;
        this.experimentsRepository = experimentsRepository;
        this.experimentGroupRepository = experimentGroupRepository;
        this.permissionsAwareExperimentRepository = permissionsAwareExperimentRepository;
    }

    public void execute() {
        Experiment experiment = permissionsAwareExperimentRepository.getExperimentOrException(experimentId);
        validate(experiment);
        ExperimentDefinition fullOnExperiment = experiment
                .getDefinition()
                .orElseThrow(() -> new UnsupportedOperationException("Missing experiment definition"))
                .makeFullOn(properties.getVariantName());
        experimentsRepository.save(fullOnExperiment);
    }

    private List<String> allVariantNames(Experiment experiment) {
        List<String> variantNames = experiment
                .getVariants()
                .stream()
                .map(ExperimentVariant::getName)
                .collect(toList());

        Optional<String> internalVariantName = experiment
                .getDefinition()
                .flatMap(ExperimentDefinition::getInternalVariantName);
        if (internalVariantName.isPresent() && !variantNames.contains(internalVariantName.get())) {
            variantNames.add(internalVariantName.get());
        }
        return variantNames;
    }

    private void validate(Experiment experiment) {
        boolean variantExists = allVariantNames(experiment)
                .stream()
                .anyMatch(it -> it.equals(properties.getVariantName()));
        if (!variantExists) {
            throw new ExperimentCommandException(
                    String.format("Experiment '%s' does not have variant named '%s'",
                            experimentId, properties.getVariantName())
            );
        }
        if (!experiment.isActive()) {
            throw new ExperimentCommandException(
                    String.format("Experiment is not ACTIVE. Now '%s' has %s status",
                            experimentId, experiment.getStatus().toString())
            );
        }
        if (experimentGroupRepository.experimentInGroup(experimentId)) {
            throw new ExperimentCommandException("Experiment cannot be made full-on if it belongs to a group");
        }
    }
}
