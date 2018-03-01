package pl.allegro.experiments.chi.chiserver.domain.experiments.administration.start;

import pl.allegro.experiments.chi.chiserver.domain.experiments.Experiment;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository;
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.PermissionsAwareExperimentRepository;

import java.util.Objects;

public class StartExperimentCommand {
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
        Experiment experiment = permissionsAwareExperimentRepository.getExperimentOrException(experimentId);
        validate(experiment);
        experimentsRepository.save(experiment.start(startExperimentProperties.getExperimentDurationDays()));
    }

    private void validate(Experiment experiment) {
        if (!experiment.isDraft()) {
            throw new StartExperimentException("Experiment is not DRAFT: " + experimentId);
        }
    }
}
