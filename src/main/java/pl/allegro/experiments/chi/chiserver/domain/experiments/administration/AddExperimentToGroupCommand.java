package pl.allegro.experiments.chi.chiserver.domain.experiments.administration;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import pl.allegro.experiments.chi.chiserver.domain.User;
import pl.allegro.experiments.chi.chiserver.domain.UserProvider;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentDefinition;
import pl.allegro.experiments.chi.chiserver.domain.experiments.Predicate;
import pl.allegro.experiments.chi.chiserver.domain.experiments.groups.AllocationTable;
import pl.allegro.experiments.chi.chiserver.domain.experiments.groups.ExperimentGroup;
import pl.allegro.experiments.chi.chiserver.domain.experiments.groups.ExperimentGroupRepository;

import java.util.List;
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
        validateEnoughPercentageSpace((List)Lists.asList(experiment.getId(),experimentGroup.getExperiments().toArray()));
        validateNextExperiment(experiment);

        return experimentGroup.addExperiment(experiment.getId());
    }

    private ExperimentGroup createGroup(final String groupId, final ExperimentDefinition experiment) {
        var salt = experiment.isDraft() ? UUID.randomUUID().toString() :experiment.getId();
        var experimentGroup = new ExperimentGroup(groupId, salt, ImmutableList.of(experiment.getId()), AllocationTable.empty());
        return experimentGroup;
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

    private void validateEnoughPercentageSpace(List<String> experimentIds) {
        Preconditions.checkArgument(!experimentIds.isEmpty());

        List<ExperimentDefinition> experiments = experimentIds.stream()
                .map(it -> permissionsAwareExperimentRepository.getExperimentOrException(it))
                .collect(toList());

        int maxBasePercentage = experiments.stream()
                .map(e -> e.getPercentage().get())
                .max(Integer::compare)
                .get();
        int percentageSum = experiments.stream()
                .mapToInt(e -> {
                    int percentage = e.getPercentage().get();
                    int numberOfVariantsDifferentThanBase = e.getVariantNames().size() - 1;
                    return numberOfVariantsDifferentThanBase * percentage;
                }).sum();

        if (maxBasePercentage + percentageSum > 100) {
            throw new ExperimentCommandException("Cannot create group - there is no enough percentage space");
        }
    }
}