package pl.allegro.experiments.chi.chiserver.domain.experiments.administration.delete;

import com.google.common.base.Preconditions;
import org.springframework.stereotype.Component;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository;
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.PermissionsAwareExperimentGetter;
import pl.allegro.experiments.chi.chiserver.domain.statistics.StatisticsRepository;

@Component
public class DeleteExperimentCommandFactory {
    private final ExperimentsRepository experimentsRepository;
    private final PermissionsAwareExperimentGetter permissionsAwareExperimentGetter;
    private final StatisticsRepository statisticsRepository;

    public DeleteExperimentCommandFactory(
            ExperimentsRepository experimentsRepository,
            PermissionsAwareExperimentGetter permissionsAwareExperimentGetter,
            StatisticsRepository statisticsRepository) {
        Preconditions.checkNotNull(experimentsRepository);
        Preconditions.checkNotNull(permissionsAwareExperimentGetter);
        Preconditions.checkNotNull(statisticsRepository);
        this.experimentsRepository = experimentsRepository;
        this.permissionsAwareExperimentGetter = permissionsAwareExperimentGetter;
        this.statisticsRepository = statisticsRepository;
    }

    public DeleteExperimentCommand deleteExperimentCommand(String experimentId) {
        Preconditions.checkNotNull(experimentId);
        return new DeleteExperimentCommand(
                experimentsRepository,
                permissionsAwareExperimentGetter,
                experimentId,
                statisticsRepository
        );
    }
}