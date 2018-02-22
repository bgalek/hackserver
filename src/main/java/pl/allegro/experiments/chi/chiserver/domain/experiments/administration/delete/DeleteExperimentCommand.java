package pl.allegro.experiments.chi.chiserver.domain.experiments.administration.delete;

import pl.allegro.experiments.chi.chiserver.domain.experiments.Experiment;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository;
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.PermissionsAwareExperimentGetter;
import pl.allegro.experiments.chi.chiserver.domain.statistics.StatisticsRepository;

public class DeleteExperimentCommand {
    private final ExperimentsRepository experimentsRepository;
    private final PermissionsAwareExperimentGetter permissionsAwareExperimentGetter;
    private final String experimentId;
    private final StatisticsRepository statisticsRepository;

    public DeleteExperimentCommand(
            ExperimentsRepository experimentsRepository,
            PermissionsAwareExperimentGetter permissionsAwareExperimentGetter,
            String experimentId,
            StatisticsRepository statisticsRepository) {
        this.experimentsRepository = experimentsRepository;
        this.permissionsAwareExperimentGetter = permissionsAwareExperimentGetter;
        this.experimentId = experimentId;
        this.statisticsRepository = statisticsRepository;
    }

    public void execute() {
        Experiment experiment = permissionsAwareExperimentGetter.getExperimentOrException(experimentId);
        validate(experiment);
        experimentsRepository.delete(experiment.getId());
    }

    private void validate(Experiment experiment) {
        if (statisticsRepository.hasAnyStatistics(experiment)) {
            throw new DeleteExperimentException("Experiment with statistics cannot be deleted: " + experimentId, null);
        }
    }
}
