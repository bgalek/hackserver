package pl.allegro.experiments.chi.chiserver.domain.experiments.administration;

import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentDefinition;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentDefinitionBuilder;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository;
import pl.allegro.experiments.chi.chiserver.domain.experiments.groups.ExperimentGroup;
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
        ExperimentDefinition experiment = permissionsAwareExperimentRepository.getExperimentOrException(experimentId);
        validate(experiment);

        ExperimentDefinition updatedExperiment = updateExperiment(experiment);
        experimentGroupRepository.findByExperimentId(experiment.getId())
                .ifPresent(g -> {
                    ExperimentGroup updatedGroup = updateGroup(updatedExperiment, g);
                    experimentGroupRepository.save(updatedGroup);
                });

        experimentsRepository.save(updatedExperiment);
    }

    private ExperimentGroup updateGroup (ExperimentDefinition experiment, ExperimentGroup group) {
        if (!group.isAllocationPossible(experiment)) {
            throw new ExperimentCommandException("not enough space in a group " + group.getId() +
                    " to add experiment " + experiment.getId());
        }

        return group.updateExperimentAllocation(experiment);
    }
    
    private ExperimentDefinition updateExperiment(ExperimentDefinition experiment){
        ExperimentDefinitionBuilder mutated = experiment.mutate()
                .percentage(properties.getPercentage())
                .deviceClass(properties.getDeviceClass().orElse(null))
                .internalVariantName(properties.getInternalVariantName().orElse(null));

        return mutated.build();
    }

    private void validate(ExperimentDefinition experiment) {
        if (experiment.isEffectivelyEnded()) {
            throw new ExperimentCommandException(experiment.getStatus() + " experiment variants cant be updated");
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
