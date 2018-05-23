package pl.allegro.experiments.chi.chiserver.domain.experiments.administration;

import pl.allegro.experiments.chi.chiserver.domain.experiments.Experiment;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentStatus;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository;
import pl.allegro.experiments.chi.chiserver.domain.experiments.groups.ExperimentGroup;
import pl.allegro.experiments.chi.chiserver.domain.experiments.groups.ExperimentGroupRepository;
import pl.allegro.experiments.chi.chiserver.domain.statistics.StatisticsRepository;

import java.util.Objects;
import java.util.Optional;

// todo refactor
public class DeleteExperimentCommand {
    private final ExperimentsRepository experimentsRepository;
    private final PermissionsAwareExperimentRepository permissionsAwareExperimentRepository;
    private final String experimentId;
    private final StatisticsRepository statisticsRepository;
    private final ExperimentGroupRepository experimentGroupRepository;

    DeleteExperimentCommand(
            ExperimentsRepository experimentsRepository,
            PermissionsAwareExperimentRepository permissionsAwareExperimentRepository,
            String experimentId,
            StatisticsRepository statisticsRepository,
            ExperimentGroupRepository experimentGroupRepository) {
        Objects.requireNonNull(experimentsRepository);
        Objects.requireNonNull(permissionsAwareExperimentRepository);
        Objects.requireNonNull(experimentId);
        Objects.requireNonNull(statisticsRepository);
        this.experimentsRepository = experimentsRepository;
        this.permissionsAwareExperimentRepository = permissionsAwareExperimentRepository;
        this.experimentId = experimentId;
        this.statisticsRepository = statisticsRepository;
        this.experimentGroupRepository = experimentGroupRepository;
    }

    public void execute() {
        Experiment experiment = permissionsAwareExperimentRepository.getExperimentOrException(experimentId);

        if (experimentGroupRepository.experimentHasGroup(experimentId) &&
                !experiment.getStatus().equals(ExperimentStatus.DRAFT)) {
            throw new ExperimentCommandException("Non DRAFT experiment bound to group cannot be deleted");
        }
        experimentGroupRepository.getExperimentGroup(experimentId)
                .map(experimentGroup -> {
                    experimentGroupRepository.save(experimentGroup.withRemovedExperiment(experimentId));
                    return null;
                });

        validate(experiment.getId());
        experimentsRepository.delete(experiment.getId());
    }

    private void validate(String experimentId) {
        if (statisticsRepository.hasAnyStatistics(experimentId)) {
            throw new ExperimentCommandException("Experiment with statistics cannot be deleted: " + experimentId);
        }
    }
}
