package pl.allegro.experiments.chi.chiserver.domain.experiments.administration.start;

import com.google.common.base.Preconditions;
import pl.allegro.experiments.chi.chiserver.domain.experiments.Experiment;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository;
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.PermissionsAwareExperimentRepository;

public class StartExperimentCommand {
    private final ExperimentsRepository experimentsRepository;
    private final StartExperimentProperties startExperimentProperties;
    private final PermissionsAwareExperimentRepository permissionsAwareExperimentRepository;
    private final String experimentId;

    public StartExperimentCommand(
            ExperimentsRepository experimentsRepository,
            StartExperimentProperties startExperimentProperties,
            PermissionsAwareExperimentRepository permissionsAwareExperimentRepository,
            String experimentId) {
        Preconditions.checkNotNull(experimentsRepository);
        Preconditions.checkNotNull(startExperimentProperties);
        Preconditions.checkNotNull(permissionsAwareExperimentRepository);
        Preconditions.checkNotNull(experimentId);
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
            throw new StartExperimentException("Experiment is not DRAFT: " + experimentId, null);
        }
        if (startExperimentProperties.getExperimentDurationDays() <= 0) {
            throw new StartExperimentException("Experiment duration days must be greater than 0: " + experimentId, null);
        }
    }
}
