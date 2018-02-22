package pl.allegro.experiments.chi.chiserver.domain.experiments.administration.start;

import org.springframework.stereotype.Component;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository;
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.PermissionsAwareExperimentGetter;

@Component
public class StartExperimentCommandFactory {
    private final ExperimentsRepository experimentsRepository;
    private final PermissionsAwareExperimentGetter permissionsAwareExperimentGetter;

    public StartExperimentCommandFactory(
            ExperimentsRepository experimentsRepository,
            PermissionsAwareExperimentGetter permissionsAwareExperimentGetter) {
        this.experimentsRepository = experimentsRepository;
        this.permissionsAwareExperimentGetter = permissionsAwareExperimentGetter;
    }

    public StartExperimentCommand startExperimentCommand(
            String experimentId,
            StartExperimentProperties properties) {
        return new StartExperimentCommand(
               experimentsRepository,
               properties,
               permissionsAwareExperimentGetter,
               experimentId
        );
    }
}
