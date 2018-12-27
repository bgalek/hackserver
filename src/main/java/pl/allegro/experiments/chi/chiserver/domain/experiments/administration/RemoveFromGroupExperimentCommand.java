package pl.allegro.experiments.chi.chiserver.domain.experiments.administration;

import com.google.common.base.Preconditions;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentDefinition;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository;
import pl.allegro.experiments.chi.chiserver.domain.experiments.groups.ExperimentGroup;
import pl.allegro.experiments.chi.chiserver.domain.experiments.groups.ExperimentGroupRepository;

public class RemoveFromGroupExperimentCommand implements ExperimentCommand {
    private final PermissionsAwareExperimentRepository permissionsAwareExperimentRepository;
    private final ExperimentGroupRepository experimentGroupRepository;
    private final String experimentId;
    private String groupName;

    public RemoveFromGroupExperimentCommand(String experimentId, ExperimentGroupRepository experimentGroupRepository, PermissionsAwareExperimentRepository permissionsAwareExperimentRepository) {
        Preconditions.checkArgument(experimentId != null);
        this.permissionsAwareExperimentRepository = permissionsAwareExperimentRepository;
        this.experimentGroupRepository = experimentGroupRepository;
        this.experimentId = experimentId;
    }

    public void execute() {
        ExperimentDefinition experiment = permissionsAwareExperimentRepository.getExperimentOrException(experimentId);

        ExperimentGroup group = experimentGroupRepository.findByExperimentId(experimentId)
                .orElseThrow(() -> new ExperimentCommandException("Experiment " + experimentId + " is not group member"));
        groupName = group.getId();

        validate(experiment);

        experimentGroupRepository.save(group.removeExperiment(experimentId));
    }

    private void validate(ExperimentDefinition experiment) {
        if (!(experiment.isDraft() || experiment.isEnded())) {
            throw new ExperimentCommandException("Only DRAFT or ENDED experiment can be removed from a group");
        }
    }

    @Override
    public String getExperimentId() {
        return experimentId;
    }

    @Override
    public String getNotificationMessage() {
        return "was removed from the group " + groupName;
    }
}
