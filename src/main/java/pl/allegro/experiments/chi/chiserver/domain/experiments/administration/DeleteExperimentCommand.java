package pl.allegro.experiments.chi.chiserver.domain.experiments.administration;

import pl.allegro.experiments.chi.chiserver.domain.experiments.Experiment;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository;
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.ExperimentCommandException;
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.PermissionsAwareExperimentRepository;
import pl.allegro.experiments.chi.chiserver.domain.statistics.StatisticsRepository;

import java.util.Objects;

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
        Objects.requireNonNull(experimentsRepository);
        Objects.requireNonNull(permissionsAwareExperimentRepository);
        Objects.requireNonNull(experimentId);
        Objects.requireNonNull(statisticsRepository);
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
            throw new ExperimentCommandException("Experiment with statistics cannot be deleted: " + experimentId);
        }
    }
}
