package pl.allegro.experiments.chi.chiserver.domain.experiments.administration;

import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentDefinition;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentTag;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentTagRepository;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository;

import java.util.stream.Collectors;

public class UpdateDescriptionsCommand implements ExperimentCommand {
    private final String experimentId;
    private final UpdateExperimentProperties properties;
    private final ExperimentsRepository experimentsRepository;
    private final ExperimentTagRepository experimentTagRepository;
    private final PermissionsAwareExperimentRepository permissionsAwareExperimentRepository;

    UpdateDescriptionsCommand(
            String experimentId,
            UpdateExperimentProperties properties,
            ExperimentsRepository experimentsRepository,
            PermissionsAwareExperimentRepository permissionsAwareExperimentRepository,
            ExperimentTagRepository experimentTagRepository) {
        this.experimentId = experimentId;
        this.properties = properties;
        this.experimentsRepository = experimentsRepository;
        this.permissionsAwareExperimentRepository = permissionsAwareExperimentRepository;
        this.experimentTagRepository = experimentTagRepository;
    }

    public void execute() {
        ExperimentDefinition experiment = permissionsAwareExperimentRepository.getExperimentOrException(experimentId);
        if (!experimentTagRepository.tagsExist(properties.getTags())) {
            throw new ExperimentCommandException("Tag does not exist");
        }

        ExperimentDefinition mutated = experiment.mutate()
                .description(properties.getDescription())
                .documentLink(properties.getDocumentLink())
                .tags(properties.getTags().stream().map(it -> new ExperimentTag(it))
                        .collect(Collectors.toList()))
                .groups(properties.getGroups()).build();
        experimentsRepository.save(mutated);
    }

    @Override
    public String getNotificationMessage() {
        return "- description was updated";
    }

    @Override
    public String getExperimentId() {
        return experimentId;
    }
}
