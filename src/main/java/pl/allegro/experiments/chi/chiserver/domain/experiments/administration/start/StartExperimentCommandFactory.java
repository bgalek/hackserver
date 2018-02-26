package pl.allegro.experiments.chi.chiserver.domain.experiments.administration.start;

import com.google.common.base.Preconditions;
import org.springframework.stereotype.Component;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository;
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.PermissionsAwareExperimentGetter;

public class StartExperimentCommandFactory {
    private final ExperimentsRepository experimentsRepository;
    private final PermissionsAwareExperimentGetter permissionsAwareExperimentGetter;

    public StartExperimentCommandFactory(
            ExperimentsRepository experimentsRepository,
            PermissionsAwareExperimentGetter permissionsAwareExperimentGetter) {
        Preconditions.checkNotNull(experimentsRepository);
        Preconditions.checkNotNull(permissionsAwareExperimentGetter);
        this.experimentsRepository = experimentsRepository;
        this.permissionsAwareExperimentGetter = permissionsAwareExperimentGetter;
    }

    public StartExperimentCommand startExperimentCommand(
            String experimentId,
            StartExperimentProperties properties) {
        Preconditions.checkNotNull(experimentId);
        Preconditions.checkNotNull(properties);
        return new StartExperimentCommand(
               experimentsRepository,
               properties,
               permissionsAwareExperimentGetter,
               experimentId
        );
    }
}
