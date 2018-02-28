package pl.allegro.experiments.chi.chiserver.domain.experiments.administration.pause;

import com.google.common.base.Preconditions;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository;
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.PermissionsAwareExperimentRepository;

public class PauseExperimentCommandFactory {
    private final ExperimentsRepository experimentsRepository;
    private final PermissionsAwareExperimentRepository permissionsAwareExperimentRepository;

    public PauseExperimentCommandFactory(
            ExperimentsRepository experimentsRepository,
            PermissionsAwareExperimentRepository permissionsAwareExperimentRepository) {
        Preconditions.checkNotNull(experimentsRepository);
        Preconditions.checkNotNull(permissionsAwareExperimentRepository);
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
