package pl.allegro.experiments.chi.chiserver.domain.experiments.administration.prolong;

import com.google.common.base.Preconditions;
import pl.allegro.experiments.chi.chiserver.domain.experiments.Experiment;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository;
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.PermissionsAwareExperimentRepository;

public class ProlongExperimentCommand {
    private final ExperimentsRepository experimentsRepository;
    private final ProlongExperimentProperties prolongExperimentProperties;
    private final PermissionsAwareExperimentRepository permissionsAwareExperimentRepository;
    private final String experimentId;

    ProlongExperimentCommand(
            ExperimentsRepository experimentsRepository,
            ProlongExperimentProperties prolongExperimentProperties,
            PermissionsAwareExperimentRepository permissionsAwareExperimentRepository,
            String experimentId) {
        Preconditions.checkNotNull(experimentsRepository);
        Preconditions.checkNotNull(prolongExperimentProperties);
        Preconditions.checkNotNull(permissionsAwareExperimentRepository);
        Preconditions.checkNotNull(experimentId);
        this.experimentId = experimentId;
        this.experimentsRepository = experimentsRepository;
        this.permissionsAwareExperimentRepository = permissionsAwareExperimentRepository;
        this.prolongExperimentProperties = prolongExperimentProperties;
    }

    public void execute() {
        Experiment experiment = permissionsAwareExperimentRepository.getExperimentOrException(experimentId);
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
