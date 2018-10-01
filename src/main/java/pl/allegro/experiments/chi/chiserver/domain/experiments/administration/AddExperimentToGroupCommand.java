package pl.allegro.experiments.chi.chiserver.domain.experiments.administration;

import pl.allegro.experiments.chi.chiserver.domain.User;
import pl.allegro.experiments.chi.chiserver.domain.UserProvider;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentDefinition;
import pl.allegro.experiments.chi.chiserver.domain.experiments.groups.ExperimentGroup;
import pl.allegro.experiments.chi.chiserver.domain.experiments.groups.ExperimentGroupRepository;

import java.util.Objects;
import java.util.UUID;

import static java.util.stream.Collectors.toList;

public class AddExperimentToGroupCommand implements Command {
    private final ExperimentGroupRepository experimentGroupRepository;
    private final PermissionsAwareExperimentRepository permissionsAwareExperimentRepository;
    private final UserProvider userProvider;
    private final AddExperimentToGroupRequest addExperimentToGroupRequest;

    AddExperimentToGroupCommand(
            ExperimentGroupRepository experimentGroupRepository,
            UserProvider userProvider,
            AddExperimentToGroupRequest addExperimentToGroupRequest,
            PermissionsAwareExperimentRepository permissionsAwareExperimentRepository) {
        Objects.requireNonNull(experimentGroupRepository);
        Objects.requireNonNull(userProvider);
        Objects.requireNonNull(addExperimentToGroupRequest);
        Objects.requireNonNull(permissionsAwareExperimentRepository);
        this.experimentGroupRepository = experimentGroupRepository;
        this.userProvider = userProvider;
        this.addExperimentToGroupRequest = addExperimentToGroupRequest;
        this.permissionsAwareExperimentRepository = permissionsAwareExperimentRepository;
    }

    public void execute() {
        User user = userProvider.getCurrentUser();
        if (!user.isLoggedIn()) {
            throw new AuthorizationException("Only logged in user can add experiments to a group");
        }

        var groupId = addExperimentToGroupRequest.getId();
        var experiment = permissionsAwareExperimentRepository.getExperimentOrException(addExperimentToGroupRequest.getExperimentId());

        validateExperiment(experiment);

        var experimentGroup = experimentGroupRepository.findById(groupId)
                .map(it -> addAnotherExperiment(it, experiment))
                .orElse(createGroup(groupId, experiment));

        experimentGroupRepository.save(experimentGroup);
    }

    private ExperimentGroup addAnotherExperiment(ExperimentGroup experimentGroup, final ExperimentDefinition experiment) {
        validatePermissionsToAllExperiments(experimentGroup);
        validateNextExperiment(experiment);

        if (!experimentGroup.checkAllocation(experiment)) {
            throw new ExperimentCommandException("not enough space in a group " + experimentGroup.getId() +
                    " to add experiment " + experiment.getId());
        }

        return experimentGroup.addExperiment(experiment);
    }

    private ExperimentGroup createGroup(final String groupId, final ExperimentDefinition experiment) {
        var salt = experiment.isDraft() ? UUID.randomUUID().toString() :experiment.getId();
        return ExperimentGroup.fromExistingExperiment(groupId, salt, experiment);
    }

    private void validateExperiment(ExperimentDefinition experiment) {
        if (experiment.isFullOn()) {
            throw new ExperimentCommandException("Full-on experiment can't be added to the group");
        }
        if (experiment.isEnded()) {
            throw new ExperimentCommandException("Ended experiment can't be added to the group");
        }
        if (experimentGroupRepository.experimentInGroup(experiment.getId())) {
            throw new ExperimentCommandException("Experiment already in another group");
        }
    }

    private void validateNextExperiment(ExperimentDefinition experiment) {
        if (!experiment.isDraft()) {
            throw new ExperimentCommandException("Can't add non-draft experiment to the existing group");
        }
    }

    private void validatePermissionsToAllExperiments(ExperimentGroup group) {
        group.getExperiments().stream()
                .map(it -> permissionsAwareExperimentRepository.getExperimentOrException(it))
                .collect(toList());
    }
}