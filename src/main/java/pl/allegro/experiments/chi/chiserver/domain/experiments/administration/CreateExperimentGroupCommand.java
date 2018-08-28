package pl.allegro.experiments.chi.chiserver.domain.experiments.administration;

import pl.allegro.experiments.chi.chiserver.domain.User;
import pl.allegro.experiments.chi.chiserver.domain.UserProvider;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentDefinition;
import pl.allegro.experiments.chi.chiserver.domain.experiments.groups.ExperimentGroup;
import pl.allegro.experiments.chi.chiserver.domain.experiments.groups.ExperimentGroupRepository;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static java.util.stream.Collectors.toList;
import static pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentStatus.DRAFT;

public class CreateExperimentGroupCommand implements Command {
    private final ExperimentGroupRepository experimentGroupRepository;
    private final PermissionsAwareExperimentRepository permissionsAwareExperimentRepository;
    private final UserProvider userProvider;
    private final ExperimentGroupCreationRequest experimentGroupCreationRequest;

    CreateExperimentGroupCommand(
            ExperimentGroupRepository experimentGroupRepository,
            UserProvider userProvider,
            ExperimentGroupCreationRequest experimentGroupCreationRequest,
            PermissionsAwareExperimentRepository permissionsAwareExperimentRepository) {
        Objects.requireNonNull(experimentGroupRepository);
        Objects.requireNonNull(userProvider);
        Objects.requireNonNull(experimentGroupCreationRequest);
        Objects.requireNonNull(permissionsAwareExperimentRepository);
        this.experimentGroupRepository = experimentGroupRepository;
        this.userProvider = userProvider;
        this.experimentGroupCreationRequest = experimentGroupCreationRequest;
        this.permissionsAwareExperimentRepository = permissionsAwareExperimentRepository;
    }

    public void execute() {
        User user = userProvider.getCurrentUser();
        if (!user.isLoggedIn()) {
            throw new AuthorizationException("Only logged in user can create experiment group");
        }
        ExperimentGroup experimentGroup = create(
                experimentGroupCreationRequest.getId(),
                experimentGroupCreationRequest.getExperiments()
        );
        experimentGroupRepository.save(experimentGroup);
    }

    private ExperimentGroup create(String groupId, List<String> experimentIds) {
        validateGroupIdIsUnique(groupId);
        List<ExperimentDefinition> experiments = validateAndGetExperiments(experimentIds);

        validateExperiments(experiments);
        return new ExperimentGroup(groupId, getSalt(experiments), experimentIds);
    }

    private List<ExperimentDefinition> validateAndGetExperiments(List<String> experimentIds) {
        try {
            return getExperimentsOrException(experimentIds);
        } catch (ExperimentNotFoundException exception) {
            throw new ExperimentCommandException("Cannot create group if not all experiments exist");
        }
    }

    private void validateExperiments(List<ExperimentDefinition> experiments) {
        validateGroupContainsAtLeast2Experiments(experiments);
        validateGroupContainsMax1NonDraftExperiment(experiments);
        validateGroupHasEnoughPercentageSpaceForAllExperiments(experiments);
        validateAllExperimentsHaveNoGroup(experiments);
        validateNoExperimentIsFullOn(experiments);
    }

    private String getSalt(List<ExperimentDefinition> experiments) {
        return experiments.stream()
                .filter(it -> !it.isDraft())
                .findFirst()
                .map(ExperimentDefinition::getId)
                .orElse(UUID.randomUUID().toString());
    }

    private void validateAllExperimentsHaveNoGroup(List<ExperimentDefinition> experiments) {
        boolean allExperimentsHaveNoGroup = experiments.stream()
                .map(ExperimentDefinition::getId)
                .noneMatch(experimentGroupRepository::experimentInGroup);
        if (!allExperimentsHaveNoGroup) {
            throw new ExperimentCommandException("Cannot create group if one of the experiments is in another group");
        }
    }

    private void validateGroupHasEnoughPercentageSpaceForAllExperiments(List<ExperimentDefinition> experiments) {
        if (!ExperimentGroup.enoughPercentageSpace(experiments)) {
            throw new ExperimentCommandException("Cannot create group there is no enough percentage space");
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

    private List<ExperimentDefinition> getExperimentsOrException(List<String> experimentIds) {
        return experimentIds.stream()
                .map(permissionsAwareExperimentRepository::getExperimentOrException)
                .collect(toList());
    }
}