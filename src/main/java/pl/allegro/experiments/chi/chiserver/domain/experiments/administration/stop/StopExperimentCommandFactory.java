package pl.allegro.experiments.chi.chiserver.domain.experiments.administration.stop;

import com.google.common.base.Preconditions;
import org.springframework.stereotype.Component;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository;
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.PermissionsAwareExperimentGetter;

@Component
public class StopExperimentCommandFactory {
    private final ExperimentsRepository experimentsRepository;
    private final PermissionsAwareExperimentGetter permissionsAwareExperimentGetter;

    public StopExperimentCommandFactory(
            ExperimentsRepository experimentsRepository,
            PermissionsAwareExperimentGetter permissionsAwareExperimentGetter) {
        Preconditions.checkNotNull(experimentsRepository);
        Preconditions.checkNotNull(permissionsAwareExperimentGetter);
        this.experimentsRepository = experimentsRepository;
        this.permissionsAwareExperimentGetter = permissionsAwareExperimentGetter;
    }

    public StopExperimentCommand stopExperimentCommand(String experimentId) {
        return new StopExperimentCommand(
                experimentId,
                experimentsRepository,
                permissionsAwareExperimentGetter
        );
    }
}
