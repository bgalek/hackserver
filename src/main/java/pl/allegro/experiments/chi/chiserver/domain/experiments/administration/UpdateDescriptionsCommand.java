package pl.allegro.experiments.chi.chiserver.domain.experiments.administration;

import pl.allegro.experiments.chi.chiserver.domain.experiments.Experiment;
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
        Experiment experiment = permissionsAwareExperimentRepository.getExperimentOrException(experimentId);

        ExperimentDefinition mutated = experiment.getDefinition()
                .orElseThrow(() -> new UnsupportedOperationException("Missing experiment definition"))
                .mutate()
                .description(properties.getDescription())
                .documentLink(properties.getDocumentLink())
                .groups(properties.getGroups()).build();

        experimentsRepository.save(mutated);
    }

    public String getNotificationMessage() {
        return "Description of experiment with '" + experimentId + "' was changed. ";
    }
}
