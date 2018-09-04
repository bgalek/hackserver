package pl.allegro.experiments.chi.chiserver.domain.experiments.administration;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import pl.allegro.experiments.chi.chiserver.domain.User;
import pl.allegro.experiments.chi.chiserver.domain.UserProvider;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentDefinition;
import pl.allegro.experiments.chi.chiserver.domain.experiments.Predicate;
import pl.allegro.experiments.chi.chiserver.domain.experiments.groups.ExperimentGroup;
import pl.allegro.experiments.chi.chiserver.domain.experiments.groups.ExperimentGroupRepository;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static java.util.stream.Collectors.toList;
import static pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentStatus.DRAFT;

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

    private void validateExperiment(ExperimentDefinition experiment) {
        if (experimentGroupRepository.experimentInGroup(experiment.getId())) {
            throw new ExperimentCommandException("Experiment already in another group");
        }
    }

    private ExperimentGroup addAnotherExperiment(ExperimentGroup experimentGroup, final ExperimentDefinition experiment) {
        validateEnoughPercentageSpace((List)Lists.asList(experiment.getId(),experimentGroup.getExperiments().toArray()));

        if (!experiment.isDraft()) {
            throw new ExperimentCommandException("Can't add non-draft experiment to existing group");
        }

        return experimentGroup.addExperiment(experiment.getId());
    }

    private ExperimentGroup createGroup(final String groupId, final ExperimentDefinition experiment) {
        var salt = experiment.isActive() ? experiment.getId() : UUID.randomUUID().toString();
        var experimentGroup = new ExperimentGroup(groupId, salt, ImmutableList.of(experiment.getId()));
        return experimentGroup;
    }

    private void validateExperiments(List<ExperimentDefinition> experiments) {
        validateGroupContainsAtLeast2Experiments(experiments);
        validateGroupContainsMax1NonDraftExperiment(experiments);
        validateNoExperimentIsFullOn(experiments);
    }

    private String getSalt(List<ExperimentDefinition> experiments) {
        return experiments.stream()
                .filter(it -> !it.isDraft())
                .findFirst()
                .map(ExperimentDefinition::getId)
                .orElse(UUID.randomUUID().toString());
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

    private void validateGroupContainsMax1NonDraftExperiment(List<ExperimentDefinition> experiments) {
        long numberOfNonDraftExperiments = experiments.stream()
                .filter(it -> !it.getStatus().equals(DRAFT))
                .count();
        if (numberOfNonDraftExperiments > 1) {
            throw new ExperimentCommandException("Cannot create group with more than 1 active experiment");
        }
    }

    private void validateGroupIdIsUnique(String id) {
        if (experimentGroupRepository.exists(id)) {
            throw new ExperimentCommandException("Provided group name is not unique");
        }
    }

    private void validateGroupContainsAtLeast2Experiments(List<ExperimentDefinition> experiments) {
        if (experiments.size() < 2) {
            throw new ExperimentCommandException("Cannot create group with less than 2 experiments");
        }
    }

    private void validateNoExperimentIsFullOn(List<ExperimentDefinition> experiments) {
        if (experiments.stream().anyMatch(ExperimentDefinition::isFullOn)) {
            throw new ExperimentCommandException("Cannot create group if one of the experiments is full-on");
        }
    }
}