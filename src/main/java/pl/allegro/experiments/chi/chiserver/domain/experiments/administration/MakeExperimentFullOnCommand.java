package pl.allegro.experiments.chi.chiserver.domain.experiments.administration;

import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentDefinition;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository;
import pl.allegro.experiments.chi.chiserver.domain.experiments.groups.ExperimentGroupRepository;

public class MakeExperimentFullOnCommand implements ExperimentCommand {
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
        ExperimentDefinition experiment = permissionsAwareExperimentRepository.getExperimentOrException(experimentId);
        validate(experiment);
        ExperimentDefinition fullOnExperiment = experiment.makeFullOn(properties.getVariantName());
        experimentsRepository.save(fullOnExperiment);
    }


    private void validate(ExperimentDefinition experiment) {
        boolean variantExists = experiment.allVariantNames().contains(properties.getVariantName());

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

    @Override
    public String getNotificationMessage() {
        return "was made full-on";
    }

    @Override
    public String getExperimentId() {
        return experimentId;
    }
}