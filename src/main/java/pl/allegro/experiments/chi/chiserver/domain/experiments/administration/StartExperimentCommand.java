package pl.allegro.experiments.chi.chiserver.domain.experiments.administration;

import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentDefinition;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository;

import java.util.Objects;

public class StartExperimentCommand implements ExperimentCommand {
    private final ExperimentsRepository experimentsRepository;
    private final StartExperimentProperties startExperimentProperties;
    private final PermissionsAwareExperimentRepository permissionsAwareExperimentRepository;
    private final String experimentId;

    StartExperimentCommand(
            ExperimentsRepository experimentsRepository,
            StartExperimentProperties startExperimentProperties,
            PermissionsAwareExperimentRepository permissionsAwareExperimentRepository,
            String experimentId) {
        Objects.requireNonNull(experimentsRepository);
        Objects.requireNonNull(startExperimentProperties);
        Objects.requireNonNull(permissionsAwareExperimentRepository);
        Objects.requireNonNull(experimentId);
        this.experimentsRepository = experimentsRepository;
        this.startExperimentProperties = startExperimentProperties;
        this.permissionsAwareExperimentRepository = permissionsAwareExperimentRepository;
        this.experimentId = experimentId;
    }

    public void execute() {
        ExperimentDefinition experiment = permissionsAwareExperimentRepository.getExperimentOrException(experimentId);
        validate(experiment);
        ExperimentDefinition started = experiment.start(startExperimentProperties.getExperimentDurationDays());
        experimentsRepository.save(started);
    }

    private void validate(ExperimentDefinition experiment) {
        if (!experiment.isDraft()) {
            throw new ExperimentCommandException("Experiment is not DRAFT: " + experimentId);
        }
    }

    @Override
    public String getNotificationMessage() {
        return "was started";
    }

    @Override
    public String getExperimentId() {
        return experimentId;
    }
}
