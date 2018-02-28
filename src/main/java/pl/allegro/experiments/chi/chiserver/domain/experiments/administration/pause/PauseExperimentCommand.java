package pl.allegro.experiments.chi.chiserver.domain.experiments.administration.pause;

import pl.allegro.experiments.chi.chiserver.domain.experiments.Experiment;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository;
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.PermissionsAwareExperimentRepository;

import java.util.Objects;

public class PauseExperimentCommand {
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
        Experiment experiment = permissionsAwareExperimentRepository.getExperimentOrException(experimentId);
        validate(experiment);
        Experiment pausedExperiment = experiment.pause();
        experimentsRepository.save(pausedExperiment);
    }

    private void validate(Experiment experiment) {
        if (experiment.isPaused()) {
            throw new PauseExperimentException(String.format("Experiment <%s> is already PAUSED.", experimentId));
        } else if (experiment.isEnded()) {
         //   throw new PauseExperimentException(String.format("Experiment <%s> is ENDED.", experimentId));
        }
    }
}