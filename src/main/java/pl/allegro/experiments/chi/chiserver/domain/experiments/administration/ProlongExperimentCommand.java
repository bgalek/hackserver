package pl.allegro.experiments.chi.chiserver.domain.experiments.administration;

import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentDefinition;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository;

import java.util.Objects;

public class ProlongExperimentCommand implements ExperimentCommand {
    private final ExperimentsRepository experimentsRepository;
    private final ProlongExperimentProperties prolongExperimentProperties;
    private final PermissionsAwareExperimentRepository permissionsAwareExperimentRepository;
    private final String experimentId;

    ProlongExperimentCommand(
            ExperimentsRepository experimentsRepository,
            ProlongExperimentProperties prolongExperimentProperties,
            PermissionsAwareExperimentRepository permissionsAwareExperimentRepository,
            String experimentId) {
        Objects.requireNonNull(experimentsRepository);
        Objects.requireNonNull(prolongExperimentProperties);
        Objects.requireNonNull(permissionsAwareExperimentRepository);
        Objects.requireNonNull(experimentId);
        this.experimentId = experimentId;
        this.experimentsRepository = experimentsRepository;
        this.permissionsAwareExperimentRepository = permissionsAwareExperimentRepository;
        this.prolongExperimentProperties = prolongExperimentProperties;
    }

    public void execute() {
        ExperimentDefinition experiment = permissionsAwareExperimentRepository.getExperimentOrException(experimentId);
        validate(experiment);
        ExperimentDefinition prolonged = experiment
                .prolong(prolongExperimentProperties.getExperimentAdditionalDays());
        experimentsRepository.save(prolonged);
    }

    private void validate(ExperimentDefinition experiment) {
        if (!experiment.isActive()) {
            throw new ExperimentCommandException("Experiment cant be prolonged if it is not ACTIVE");
        }
    }

    @Override
    public String getNotificationMessage() {
        return "was prolonged by " + prolongExperimentProperties.getExperimentAdditionalDays() + " days";
    }

    @Override
    public String getExperimentId() {
        return experimentId;
    }
}
