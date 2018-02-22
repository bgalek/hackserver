package pl.allegro.experiments.chi.chiserver.domain.experiments.administration.stop;

import pl.allegro.experiments.chi.chiserver.domain.experiments.Experiment;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository;
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.PermissionsAwareExperimentGetter;

public class StopExperimentCommand {
    private final String experimentId;
    private final ExperimentsRepository experimentsRepository;
    private final PermissionsAwareExperimentGetter permissionsAwareExperimentGetter;

    public StopExperimentCommand(
            String experimentId,
            ExperimentsRepository experimentsRepository,
            PermissionsAwareExperimentGetter permissionsAwareExperimentGetter) {
        this.experimentId = experimentId;
        this.experimentsRepository = experimentsRepository;
        this.permissionsAwareExperimentGetter = permissionsAwareExperimentGetter;
    }

    public void execute() {
        Experiment experiment = permissionsAwareExperimentGetter.getExperimentOrException(experimentId);
        validate(experiment);
        Experiment stoppedExperiment = experiment.stop();
        experimentsRepository.save(stoppedExperiment);
    }

    private void validate(Experiment experiment) {
        if (!experiment.isActive()) {
            throw new StopExperimentException("Experiment is not ACTIVE: " + experimentId, null);
        }
    }
}