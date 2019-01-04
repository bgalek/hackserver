package pl.allegro.experiments.chi.chiserver.domain.experiments.administration;

import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentDefinition;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository;

import java.util.Objects;

public class PauseExperimentCommand implements ExperimentCommand {
    private final String experimentId;
    private final ExperimentsRepository experimentsRepository;
    private final PermissionsAwareExperimentRepository permissionsAwareExperimentRepository;

    PauseExperimentCommand(
            String experimentId,
            ExperimentsRepository experimentsRepository,
            PermissionsAwareExperimentRepository permissionsAwareExperimentRepository) {
        Objects.requireNonNull(experimentId);
        Objects.requireNonNull(experimentsRepository);
        Objects.requireNonNull(permissionsAwareExperimentRepository);
        this.experimentId = experimentId;
        this.experimentsRepository = experimentsRepository;
        this.permissionsAwareExperimentRepository = permissionsAwareExperimentRepository;
    }

    public void execute() {
        ExperimentDefinition experiment = permissionsAwareExperimentRepository.getExperimentOrException(experimentId);
        validate(experiment);
        ExperimentDefinition pausedExperiment = experiment.pause();
        experimentsRepository.save(pausedExperiment);
    }

    private void validate(ExperimentDefinition experiment) {
        if (!experiment.isActive()) {
            throw new ExperimentCommandException(
                    String.format("Experiment is not ACTIVE. Now '%s' has %s status",
                            experimentId, experiment.getStatus().toString())
            );
        }
    }

    @Override
    public String getNotificationMessage() {
        return "was paused";
    }

    @Override
    public String getExperimentId() {
        return experimentId;
    }
}