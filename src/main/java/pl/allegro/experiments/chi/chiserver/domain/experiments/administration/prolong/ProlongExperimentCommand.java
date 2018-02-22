package pl.allegro.experiments.chi.chiserver.domain.experiments.administration.prolong;

import pl.allegro.experiments.chi.chiserver.domain.experiments.Experiment;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository;
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.PermissionsAwareExperimentGetter;

public class ProlongExperimentCommand {
    private final ExperimentsRepository experimentsRepository;
    private final ProlongExperimentProperties prolongExperimentProperties;
    private final PermissionsAwareExperimentGetter permissionsAwareExperimentGetter;
    private final String experimentId;

    public ProlongExperimentCommand(
            ExperimentsRepository experimentsRepository,
            ProlongExperimentProperties prolongExperimentProperties,
            PermissionsAwareExperimentGetter permissionsAwareExperimentGetter,
            String experimentId) {
        this.experimentId = experimentId;
        this.experimentsRepository = experimentsRepository;
        this.permissionsAwareExperimentGetter = permissionsAwareExperimentGetter;
        this.prolongExperimentProperties = prolongExperimentProperties;
    }

    public void execute() {
        Experiment experiment = permissionsAwareExperimentGetter.getExperimentOrException(experimentId);
        validate(experiment);
        experimentsRepository.save(experiment.prolong(prolongExperimentProperties.getExperimentAdditionalDays()));
    }

    private void validate(Experiment experiment) {
        if (!experiment.isActive()) {
            throw new ProlongExperimentException("Experiment additional days must be greater than 0: " + experimentId, null);
        }
        if (prolongExperimentProperties.getExperimentAdditionalDays() <= 0) {
            throw new ProlongExperimentException("Experiment additional days must be greater than 0: " + experimentId, null);
        }
    }
}
