package pl.allegro.experiments.chi.chiserver.domain.experiments.administration.prolong;

import org.springframework.stereotype.Component;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository;
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.PermissionsAwareExperimentGetter;

@Component
public class ProlongExperimentCommandFactory {
    private final ExperimentsRepository experimentsRepository;
    private final PermissionsAwareExperimentGetter permissionsAwareExperimentGetter;

    public ProlongExperimentCommandFactory(
            ExperimentsRepository experimentsRepository,
            PermissionsAwareExperimentGetter permissionsAwareExperimentGetter) {
        this.experimentsRepository = experimentsRepository;
        this.permissionsAwareExperimentGetter = permissionsAwareExperimentGetter;
    }

    public ProlongExperimentCommand prolongExperimentCommand(String experimentId, ProlongExperimentProperties properties) {
        return new ProlongExperimentCommand(
                experimentsRepository,
                properties,
                permissionsAwareExperimentGetter,
                experimentId
        );
    }
}
