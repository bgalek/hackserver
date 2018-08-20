package pl.allegro.experiments.chi.chiserver.domain.experiments.administration;

import pl.allegro.experiments.chi.chiserver.domain.experiments.Experiment;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentDefinition;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository;
import pl.allegro.experiments.chi.chiserver.domain.experiments.groups.ExperimentGroupRepository;

public class UpdateVariantsCommand implements ExperimentCommand {
    private final String experimentId;
    private final UpdateVariantsProperties properties;
    private final ExperimentsRepository experimentsRepository;
    private final PermissionsAwareExperimentRepository permissionsAwareExperimentRepository;
    private final ExperimentGroupRepository experimentGroupRepository;

    UpdateVariantsCommand(
            String experimentId,
            UpdateVariantsProperties properties,
            ExperimentsRepository experimentsRepository,
            PermissionsAwareExperimentRepository permissionsAwareExperimentRepository,
            ExperimentGroupRepository experimentGroupRepository) {
        this.experimentId = experimentId;
        this.properties = properties;
        this.experimentsRepository = experimentsRepository;
        this.permissionsAwareExperimentRepository = permissionsAwareExperimentRepository;
        this.experimentGroupRepository = experimentGroupRepository;
    }

    public void execute() {
        Experiment experiment = permissionsAwareExperimentRepository.getExperimentOrException(experimentId);
        validate(experiment);
        ExperimentDefinition mutated = experiment.getDefinition()
                .orElseThrow(() -> new UnsupportedOperationException("Missing experiment definition"))
                .mutate()
                .percentage(properties.getPercentage())
                .deviceClass(properties.getDeviceClass().orElse(null))
                .internalVariantName(properties.getInternalVariantName().orElse(null))
                .variantNames(properties.getVariantNames())
                .build();
        experimentsRepository.save(mutated);
    }

    private void validate(Experiment experiment) {
        if (experiment.isEffectivelyEnded()) {
            throw new ExperimentCommandException(experiment.getStatus() + " experiment variants cant be updated");
        }

        if (experimentGroupRepository.experimentInGroup(experimentId)) {
            throw new ExperimentCommandException("Can not change variants of experiment bounded to a group");
        }
    }

    public String getNotificationMessage() {
        return "- traffic allocation was changed to "+properties.getPercentage()+"%";
    }

    @Override
    public String getExperimentId() {
        return experimentId;
    }
}
