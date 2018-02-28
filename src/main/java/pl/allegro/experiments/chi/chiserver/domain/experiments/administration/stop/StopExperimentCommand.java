package pl.allegro.experiments.chi.chiserver.domain.experiments.administration.stop;

import com.google.common.base.Preconditions;
import pl.allegro.experiments.chi.chiserver.domain.experiments.Experiment;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository;
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.PermissionsAwareExperimentRepository;

public class StopExperimentCommand {
    private final String experimentId;
    private final ExperimentsRepository experimentsRepository;
    private final PermissionsAwareExperimentRepository permissionsAwareExperimentRepository;

    StopExperimentCommand(
            String experimentId,
            ExperimentsRepository experimentsRepository,
            PermissionsAwareExperimentRepository permissionsAwareExperimentRepository) {
        Preconditions.checkNotNull(experimentId);
        Preconditions.checkNotNull(experimentsRepository);
        Preconditions.checkNotNull(permissionsAwareExperimentRepository);
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
            throw new StopExperimentException("Experiment is not ACTIVE: " + experimentId);
        }
    }
}