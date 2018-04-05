package pl.allegro.experiments.chi.chiserver.domain.experiments.administration;

import pl.allegro.experiments.chi.chiserver.domain.experiments.Experiment;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentDefinition;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository;

public class UpdateVariantsCommand {
    private final String experimentId;
    private final UpdateVariantsProperties properties;
    private final ExperimentsRepository experimentsRepository;
    private final PermissionsAwareExperimentRepository permissionsAwareExperimentRepository;

    UpdateVariantsCommand(String experimentId, UpdateVariantsProperties properties, ExperimentsRepository experimentsRepository, PermissionsAwareExperimentRepository permissionsAwareExperimentRepository) {
        this.experimentId = experimentId;
        this.properties = properties;
        this.experimentsRepository = experimentsRepository;
        this.permissionsAwareExperimentRepository = permissionsAwareExperimentRepository;
    }

    public void execute() {
        Experiment experiment = permissionsAwareExperimentRepository.getExperimentOrException(experimentId);

        ExperimentDefinition mutated = experiment.getDefinition()
                .orElseThrow(() -> new UnsupportedOperationException("Missing experiment definition"))
                .mutate()
                .percentage(properties.getPercentage())
                .deviceClass(properties.getDeviceClass().orElse(null))
                .internalVariantName(properties.getInternalVariantName().orElse(null))
                .variantNames(properties.getVariantNames())
                .build();

        experimentsRepository.save(mutated);
    }
}
