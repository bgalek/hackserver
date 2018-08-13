package pl.allegro.experiments.chi.chiserver.domain.experiments.administration;

import pl.allegro.experiments.chi.chiserver.domain.experiments.Experiment;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentDefinition;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository;
import pl.allegro.experiments.chi.chiserver.domain.experiments.groups.ExperimentGroupRepository;

import java.util.Objects;

public class StartExperimentCommand {
    private final ExperimentsRepository experimentsRepository;
    private final StartExperimentProperties startExperimentProperties;
    private final PermissionsAwareExperimentRepository permissionsAwareExperimentRepository;
    private final String experimentId;
    private final ExperimentGroupRepository experimentGroupRepository;

    StartExperimentCommand(
            ExperimentsRepository experimentsRepository,
            StartExperimentProperties startExperimentProperties,
            PermissionsAwareExperimentRepository permissionsAwareExperimentRepository,
            ExperimentGroupRepository experimentGroupRepository,
            String experimentId) {
        Objects.requireNonNull(experimentsRepository);
        Objects.requireNonNull(startExperimentProperties);
        Objects.requireNonNull(permissionsAwareExperimentRepository);
        Objects.requireNonNull(experimentGroupRepository);
        Objects.requireNonNull(experimentId);
        this.experimentsRepository = experimentsRepository;
        this.startExperimentProperties = startExperimentProperties;
        this.permissionsAwareExperimentRepository = permissionsAwareExperimentRepository;
        this.experimentId = experimentId;
        this.experimentGroupRepository = experimentGroupRepository;
    }

    public void execute() {
        Experiment experiment = permissionsAwareExperimentRepository.getExperimentOrException(experimentId);
        validate(experiment);
        ExperimentDefinition started = experiment
                .getDefinition()
                .orElseThrow(() -> new UnsupportedOperationException("Missing experiment definition"))
                .start(startExperimentProperties.getExperimentDurationDays());
        experimentGroupRepository.findByExperimentId(experimentId)
                .ifPresent(experimentGroup ->
                    experimentGroupRepository
                            .save(experimentGroup.moveExperimentToTail(experimentId))
                );
        experimentsRepository.save(started);
    }

    private void validate(Experiment experiment) {
        if (!experiment.isDraft()) {
            throw new ExperimentCommandException("Experiment is not DRAFT: " + experimentId);
        }
    }

    String getNotificationMessage(String experimentId) {
        return "Experiment with id '" + experimentId + "' was started.";
    }
}
