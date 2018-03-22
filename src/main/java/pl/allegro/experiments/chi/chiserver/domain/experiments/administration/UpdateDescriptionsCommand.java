package pl.allegro.experiments.chi.chiserver.domain.experiments.administration;

import pl.allegro.experiments.chi.chiserver.domain.experiments.Experiment;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository;

public class UpdateDescriptionsCommand {
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

        Experiment mutated = experiment.mutate()
                  .description(properties.getDescription())
                  .documentLink(properties.getDocumentLink())
                  .groups(properties.getGroups()).build();

        experimentsRepository.save(mutated);
    }
}
