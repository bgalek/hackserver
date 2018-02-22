package pl.allegro.experiments.chi.chiserver.domain.experiments.administration.start;

import pl.allegro.experiments.chi.chiserver.domain.experiments.Experiment;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository;
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.PermissionsAwareExperimentGetter;

public class StartExperimentCommand {
    private final ExperimentsRepository experimentsRepository;
    private final StartExperimentProperties startExperimentProperties;
    private final PermissionsAwareExperimentGetter permissionsAwareExperimentGetter;
    private final String experimentId;

    public StartExperimentCommand(
            ExperimentsRepository experimentsRepository,
            StartExperimentProperties startExperimentProperties,
            PermissionsAwareExperimentGetter permissionsAwareExperimentGetter,
            String experimentId) {
        this.experimentsRepository = experimentsRepository;
        this.startExperimentProperties = startExperimentProperties;
        this.permissionsAwareExperimentGetter = permissionsAwareExperimentGetter;
        this.experimentId = experimentId;
    }

    public void execute() {
        Experiment experiment = permissionsAwareExperimentGetter.getExperimentOrException(experimentId);
        validate(experiment);
        experimentsRepository.save(experiment.start(startExperimentProperties.getExperimentDurationDays()));
    }

    private void validate(Experiment experiment) {
        if (!experiment.isDraft()) {
            throw new StartExperimentException("Experiment is not DRAFT: " + experimentId, null);
        }
        if (startExperimentProperties.getExperimentDurationDays() <= 0) {
            throw new StartExperimentException("Experiment duration days must be greater than 0: " + experimentId, null);
        }
    }
}
