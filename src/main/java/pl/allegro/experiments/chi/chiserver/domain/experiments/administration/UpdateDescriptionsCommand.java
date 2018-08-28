package pl.allegro.experiments.chi.chiserver.domain.experiments.administration;

import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentDefinition;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository;

public class UpdateDescriptionsCommand implements ExperimentCommand {
    private final String experimentId;
    private final UpdateExperimentProperties properties;
    private final ExperimentsRepository experimentsRepository;
    private final PermissionsAwareExperimentRepository permissionsAwareExperimentRepository;

    UpdateDescriptionsCommand(String experimentId, UpdateExperimentProperties properties, ExperimentsRepository experimentsRepository, PermissionsAwareExperimentRepository permissionsAwareExperimentRepository) {
        this.experimentId = experimentId;
        this.properties = properties;
        this.experimentsRepository = experimentsRepository;
        this.permissionsAwareExperimentRepository = permissionsAwareExperimentRepository;
    }

    public void execute() {
        ExperimentDefinition experiment = permissionsAwareExperimentRepository.getExperimentOrException(experimentId);
        ExperimentDefinition mutated = experiment.mutate()
                .description(properties.getDescription())
                .documentLink(properties.getDocumentLink())
                .groups(properties.getGroups()).build();
        experimentsRepository.save(mutated);
    }

    @Override
    public String getNotificationMessage() {
        return "- description was updated";
    }

    @Override
    public String getExperimentId() {
        return experimentId;
    }
}
