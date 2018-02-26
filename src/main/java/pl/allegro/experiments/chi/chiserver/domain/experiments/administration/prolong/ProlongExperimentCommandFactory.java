package pl.allegro.experiments.chi.chiserver.domain.experiments.administration.prolong;

import com.google.common.base.Preconditions;
import org.springframework.stereotype.Component;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository;
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.PermissionsAwareExperimentGetter;

public class ProlongExperimentCommandFactory {
    private final ExperimentsRepository experimentsRepository;
    private final PermissionsAwareExperimentGetter permissionsAwareExperimentGetter;

    public ProlongExperimentCommandFactory(
            ExperimentsRepository experimentsRepository,
            PermissionsAwareExperimentGetter permissionsAwareExperimentGetter) {
        Preconditions.checkNotNull(experimentsRepository);
        Preconditions.checkNotNull(permissionsAwareExperimentGetter);
        this.experimentsRepository = experimentsRepository;
        this.permissionsAwareExperimentGetter = permissionsAwareExperimentGetter;
    }

    public ProlongExperimentCommand prolongExperimentCommand(
            String experimentId,
            ProlongExperimentProperties properties) {
        Preconditions.checkNotNull(experimentId);
        Preconditions.checkNotNull(properties);
        return new ProlongExperimentCommand(
                experimentsRepository,
                properties,
                permissionsAwareExperimentGetter,
                experimentId
        );
    }
}
