package pl.allegro.experiments.chi.chiserver.domain.experiments.administration.delete;

import com.google.common.base.Preconditions;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository;
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.PermissionsAwareExperimentRepository;
import pl.allegro.experiments.chi.chiserver.domain.statistics.StatisticsRepository;

public class DeleteExperimentCommandFactory {
    private final ExperimentsRepository experimentsRepository;
    private final PermissionsAwareExperimentRepository permissionsAwareExperimentRepository;
    private final StatisticsRepository statisticsRepository;

    public DeleteExperimentCommandFactory(
            ExperimentsRepository experimentsRepository,
            PermissionsAwareExperimentRepository permissionsAwareExperimentRepository,
            StatisticsRepository statisticsRepository) {
        Preconditions.checkNotNull(experimentsRepository);
        Preconditions.checkNotNull(permissionsAwareExperimentRepository);
        Preconditions.checkNotNull(statisticsRepository);
        this.experimentsRepository = experimentsRepository;
        this.permissionsAwareExperimentRepository = permissionsAwareExperimentRepository;
        this.statisticsRepository = statisticsRepository;
    }

    public DeleteExperimentCommand deleteExperimentCommand(String experimentId) {
        Preconditions.checkNotNull(experimentId);
        return new DeleteExperimentCommand(
                experimentsRepository,
                permissionsAwareExperimentRepository,
                experimentId,
                statisticsRepository
        );
    }
}