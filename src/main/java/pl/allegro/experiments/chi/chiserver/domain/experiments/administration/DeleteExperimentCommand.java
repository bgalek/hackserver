package pl.allegro.experiments.chi.chiserver.domain.experiments.administration;

import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentDefinition;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository;
import pl.allegro.experiments.chi.chiserver.domain.experiments.groups.ExperimentGroupRepository;
import pl.allegro.experiments.chi.chiserver.domain.statistics.classic.StatisticsRepository;

import java.util.Objects;

public class DeleteExperimentCommand implements ExperimentCommand {
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
        ExperimentDefinition experiment = permissionsAwareExperimentRepository.getExperimentOrException(experimentId);
        validate(experiment);
        experimentGroupRepository.findByExperimentId(experimentId)
                .ifPresent(experimentGroup ->
                    experimentGroupRepository.save(experimentGroup.removeExperiment(experimentId))
                );
        experimentsRepository.delete(experiment.getId());
    }

    private void validate(ExperimentDefinition experiment) {
        if (statisticsRepository.hasAnyStatistics(experimentId)) {
            throw new ExperimentCommandException("Experiment with statistics cannot be deleted: " + experimentId);
        }
        if (experimentGroupRepository.experimentInGroup(experimentId) && !experiment.isDraft()) {
            throw new ExperimentCommandException("Non-DRAFT experiment bounded to a group can not be deleted");
        }
    }

    @Override
    public String getNotificationMessage() {
        return "was deleted";
    }

    @Override
    public String getExperimentId() {
        return experimentId;
    }
}
