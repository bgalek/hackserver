package pl.allegro.experiments.chi.chiserver.domain.experiments.administration;

import pl.allegro.experiments.chi.chiserver.domain.experiments.*;

import java.util.List;

public class UpdateExperimentEventDefinitionsCommand {
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
        Experiment experiment = permissionsAwareExperimentRepository.getExperimentOrException(experimentId);
        if (!experiment.getReportingDefinition().getType().equals(ReportingType.FRONTEND)) {
            throw new ExperimentCommandException("Non frontend experiment has no event definitions");
        }
        if (experiment.getStatus().equals(ExperimentStatus.ENDED)) {
            throw new ExperimentCommandException("ENDED experiment event definitions cant be updated");
        }

        ExperimentDefinition mutated = experiment.getDefinition()
                .orElseThrow(() -> new UnsupportedOperationException("Missing experiment definition"))
                .mutate()
                .reportingDefinition(ReportingDefinition.frontend(eventDefinitions))
                .build();

        experimentsRepository.save(mutated);
    }
}
