package pl.allegro.experiments.chi.chiserver.domain.experiments.administration;

import pl.allegro.experiments.chi.chiserver.domain.experiments.Experiment;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository;
import pl.allegro.experiments.chi.chiserver.domain.experiments.groups.ExperimentGroupRepository;
import pl.allegro.experiments.chi.chiserver.domain.statistics.clasic.StatisticsRepository;

import java.util.Objects;

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
        Objects.requireNonNull(experimentGroupRepository);
        this.experimentsRepository = experimentsRepository;
        this.permissionsAwareExperimentRepository = permissionsAwareExperimentRepository;
        this.experimentId = experimentId;
        this.statisticsRepository = statisticsRepository;
        this.experimentGroupRepository = experimentGroupRepository;
    }

    public void execute() {
        Experiment experiment = permissionsAwareExperimentRepository.getExperimentOrException(experimentId);
        validate(experiment);

        experimentGroupRepository.findByExperimentId(experimentId)
                .ifPresent(experimentGroup ->
                    experimentGroupRepository.save(experimentGroup.removeExperiment(experimentId))
                );

        experimentsRepository.delete(experiment.getId());
    }

    private void validate(Experiment experiment) {
        if (statisticsRepository.hasAnyStatistics(experimentId)) {
            throw new ExperimentCommandException("Experiment with statistics cannot be deleted: " + experimentId);
        }
        if (experimentGroupRepository.experimentInGroup(experimentId) &&
                !experiment.isDraft()) {
            throw new ExperimentCommandException("Non-DRAFT experiment bounded to a group can not be deleted");
        }
    }
}
