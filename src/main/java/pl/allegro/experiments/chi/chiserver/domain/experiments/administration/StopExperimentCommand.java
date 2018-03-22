package pl.allegro.experiments.chi.chiserver.domain.experiments.administration;

import pl.allegro.experiments.chi.chiserver.domain.experiments.Experiment;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository;
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.ExperimentCommandException;
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.PermissionsAwareExperimentRepository;

import java.util.Objects;

public class StopExperimentCommand {
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
        Experiment experiment = permissionsAwareExperimentRepository.getExperimentOrException(experimentId);
        validate(experiment);
        Experiment stoppedExperiment = experiment.stop();
        experimentsRepository.save(stoppedExperiment);
    }

    private void validate(Experiment experiment) {
        if (!experiment.isActive()) {
            throw new ExperimentCommandException("Experiment is not ACTIVE: " + experimentId);
        }
    }
}