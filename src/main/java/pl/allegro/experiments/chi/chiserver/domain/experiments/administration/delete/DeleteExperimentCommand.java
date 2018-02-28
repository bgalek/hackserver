package pl.allegro.experiments.chi.chiserver.domain.experiments.administration.delete;

import com.google.common.base.Preconditions;
import pl.allegro.experiments.chi.chiserver.domain.experiments.Experiment;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository;
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.PermissionsAwareExperimentRepository;
import pl.allegro.experiments.chi.chiserver.domain.statistics.StatisticsRepository;

public class DeleteExperimentCommand {
    private final ExperimentsRepository experimentsRepository;
    private final PermissionsAwareExperimentRepository permissionsAwareExperimentRepository;
    private final String experimentId;
    private final StatisticsRepository statisticsRepository;

    DeleteExperimentCommand(
            ExperimentsRepository experimentsRepository,
            PermissionsAwareExperimentRepository permissionsAwareExperimentRepository,
            String experimentId,
            StatisticsRepository statisticsRepository) {
        Preconditions.checkNotNull(experimentsRepository);
        Preconditions.checkNotNull(permissionsAwareExperimentRepository);
        Preconditions.checkNotNull(experimentId);
        Preconditions.checkNotNull(statisticsRepository);
        this.experimentsRepository = experimentsRepository;
        this.permissionsAwareExperimentRepository = permissionsAwareExperimentRepository;
        this.experimentId = experimentId;
        this.statisticsRepository = statisticsRepository;
    }

    public void execute() {
        Experiment experiment = permissionsAwareExperimentRepository.getExperimentOrException(experimentId);
        validate(experiment);
        experimentsRepository.delete(experiment.getId());
    }

    private void validate(Experiment experiment) {
        if (statisticsRepository.hasAnyStatistics(experiment)) {
            throw new DeleteExperimentException("Experiment with statistics cannot be deleted: " + experimentId);
        }
    }
}
