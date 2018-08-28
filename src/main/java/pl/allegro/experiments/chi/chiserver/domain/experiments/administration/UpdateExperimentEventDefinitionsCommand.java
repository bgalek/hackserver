package pl.allegro.experiments.chi.chiserver.domain.experiments.administration;

import pl.allegro.experiments.chi.chiserver.domain.experiments.*;

import java.util.List;

public class UpdateExperimentEventDefinitionsCommand implements ExperimentCommand {
    private final String experimentId;
    private final ExperimentsRepository experimentsRepository;
    private final PermissionsAwareExperimentRepository permissionsAwareExperimentRepository;
    private final List<EventDefinition> eventDefinitions;

    public UpdateExperimentEventDefinitionsCommand(
            String experimentId,
            ExperimentsRepository experimentsRepository,
            PermissionsAwareExperimentRepository permissionsAwareExperimentRepository,
            List<EventDefinition> eventDefinitions) {
        this.experimentId = experimentId;
        this.experimentsRepository = experimentsRepository;
        this.permissionsAwareExperimentRepository = permissionsAwareExperimentRepository;
        this.eventDefinitions = eventDefinitions;
    }

    public void execute() {
        ExperimentDefinition experiment = permissionsAwareExperimentRepository.getExperimentOrException(experimentId);
        validate(experiment);
        ExperimentDefinition mutated = experiment.mutate()
                .reportingDefinition(ReportingDefinition.frontend(eventDefinitions))
                .build();

        experimentsRepository.save(mutated);
    }

    private void validate(ExperimentDefinition experiment) {
        if (!experiment.getReportingDefinition().getType().equals(ReportingType.FRONTEND)) {
            throw new ExperimentCommandException("Non frontend experiment has no event definitions");
        }
        if (experiment.isEffectivelyEnded()) {
            throw new ExperimentCommandException(experiment.getStatus() + " experiment event definitions cant be updated");
        }
    }

    public String getNotificationMessage() {
        return "- event definitions was updated";
    }

    @Override
    public String getExperimentId() {
        return experimentId;
    }
}
