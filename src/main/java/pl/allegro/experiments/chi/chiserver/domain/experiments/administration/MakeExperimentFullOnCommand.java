package pl.allegro.experiments.chi.chiserver.domain.experiments.administration;

import pl.allegro.experiments.chi.chiserver.domain.experiments.Experiment;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentDefinition;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentVariant;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository;
import pl.allegro.experiments.chi.chiserver.domain.experiments.groups.ExperimentGroupRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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

    private Set<String> allVariantNames(Experiment experiment) {
        return experiment.getDefinition().map(d -> d.allVariantNames()).orElse(Collections.emptySet());
    }

    private void validate(Experiment experiment) {
        boolean variantExists = allVariantNames(experiment).contains(properties.getVariantName());

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
