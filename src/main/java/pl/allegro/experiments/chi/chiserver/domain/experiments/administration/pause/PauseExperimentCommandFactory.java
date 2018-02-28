package pl.allegro.experiments.chi.chiserver.domain.experiments.administration.pause;

import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository;
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.PermissionsAwareExperimentRepository;

import java.util.Objects;

public class PauseExperimentCommandFactory {
    private final ExperimentsRepository experimentsRepository;
    private final PermissionsAwareExperimentRepository permissionsAwareExperimentRepository;

    public PauseExperimentCommandFactory(
            ExperimentsRepository experimentsRepository,
            PermissionsAwareExperimentRepository permissionsAwareExperimentRepository) {
        Objects.requireNonNull(experimentsRepository);
        Objects.requireNonNull(permissionsAwareExperimentRepository);
        this.experimentsRepository = experimentsRepository;
        this.permissionsAwareExperimentRepository = permissionsAwareExperimentRepository;
    }

    public PauseExperimentCommand pauseExperimentCommand(String experimentId) {
        return new PauseExperimentCommand(
                experimentId,
                experimentsRepository,
                permissionsAwareExperimentRepository
        );
    }
}
