package pl.allegro.experiments.chi.chiserver.domain.experiments.administration;

import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentDefinition;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository;

import java.util.Objects;

public class StopExperimentCommand implements ExperimentCommand {
    private final String experimentId;
    private final ExperimentsRepository experimentsRepository;
    private final PermissionsAwareExperimentRepository permissionsAwareExperimentRepository;

    StopExperimentCommand(
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
        ExperimentDefinition stoppedExperiment = experiment.stop();
        experimentsRepository.save(stoppedExperiment);
    }

    private void validate(ExperimentDefinition experiment) {
        if (!experiment.isEndable()) {
            throw new ExperimentCommandException(experiment.getStatus() + " experiment cannot be ended");
        }
    }

    @Override
    public String getNotificationMessage() {
        return "was stopped";
    }

    @Override
    public String getExperimentId() {
        return experimentId;
    }
}