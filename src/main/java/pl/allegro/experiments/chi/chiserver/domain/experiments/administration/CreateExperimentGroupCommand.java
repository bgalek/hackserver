package pl.allegro.experiments.chi.chiserver.domain.experiments.administration;

import pl.allegro.experiments.chi.chiserver.domain.User;
import pl.allegro.experiments.chi.chiserver.domain.UserProvider;
import pl.allegro.experiments.chi.chiserver.domain.experiments.Experiment;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentStatus;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository;
import pl.allegro.experiments.chi.chiserver.domain.experiments.groups.ExperimentGroup;
import pl.allegro.experiments.chi.chiserver.domain.experiments.groups.ExperimentGroupRepository;
import pl.allegro.experiments.chi.chiserver.infrastructure.experiments.ExperimentOrigin;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class CreateExperimentGroupCommand implements ExperimentCommand {
    private final ExperimentGroupRepository experimentGroupRepository;
    private final ExperimentsRepository experimentsRepository;
    private final PermissionsAwareExperimentRepository permissionsAwareExperimentRepository;
    private final UserProvider userProvider;
    private final ExperimentGroupCreationRequest experimentGroupCreationRequest;

    CreateExperimentGroupCommand(
            ExperimentGroupRepository experimentGroupRepository,
            ExperimentsRepository experimentsRepository,
            UserProvider userProvider,
            ExperimentGroupCreationRequest experimentGroupCreationRequest,
            PermissionsAwareExperimentRepository permissionsAwareExperimentRepository) {
        Objects.requireNonNull(experimentGroupRepository);
        Objects.requireNonNull(userProvider);
        Objects.requireNonNull(experimentsRepository);
        Objects.requireNonNull(experimentGroupCreationRequest);
        Objects.requireNonNull(permissionsAwareExperimentRepository);
        this.experimentGroupRepository = experimentGroupRepository;
        this.userProvider = userProvider;
        this.experimentsRepository = experimentsRepository;
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

    private ExperimentGroup create(String id, List<String> experiments) {
        // legacy mongo experiments are not supported

        checkIfGroupNameIsUnique(id);

        checkIfGroupContainsAtLeast2Experiments(experiments);

        checkIfAllExperimentsInGroupExist(experiments);

        checkIfGroupContainsMax1NonDraftExperiment(experiments);

        checkIfGroupDoesNotContainsStashExperiments(experiments);

        permissionsAwareExperimentRepository.checkIfUserHasPermissionsToAllExperiments(experiments);

        checkIfGroupHasEnoughPercentageSpaceForAllExperiments(experiments);

        checkIfAllExperimentsHaveNoGroup(experiments);

        checkIfNoExperimentIsFullOn(experiments);

        return new ExperimentGroup(id, getSalt(experiments), experiments);
    }

    private String getSalt(List<String> experiments) {
        return experiments.stream()
                .map(experimentsRepository::getExperiment)
                .filter(experiment ->
                    experiment.isPresent() &&
                            !experiment.get().isDraft()
                ).map(experiment -> experiment.get())
                .findFirst()
                .map(experiment -> experiment.getId())
                .orElse(UUID.randomUUID().toString());
    }

    private void checkIfAllExperimentsHaveNoGroup(List<String> experiments) {
        if (experiments.stream().anyMatch(e -> experimentGroupRepository.experimentInGroup(e))) {
            throw new ExperimentCommandException("Cannot create group if one of the experiments is in another group");
        }
    }

    private void checkIfGroupHasEnoughPercentageSpaceForAllExperiments(List<String> experiments) {
        if (!enoughPercentageSpace(experiments)) {
            throw new ExperimentCommandException("Cannot create group there is no enough percentage space");
        }
    }

    private void checkIfGroupDoesNotContainsStashExperiments(List<String> experiments) {
        boolean stashExperimentPresent = experiments.stream()
                .map(experimentsRepository::getExperiment)
                .map(e -> experimentsRepository.getOrigin(e.get().getId()))
                .anyMatch(origin -> origin.equals(ExperimentOrigin.STASH));

        if (stashExperimentPresent) {
            throw new ExperimentCommandException("Cannot create group with stash experiments");
        }
    }

    private void checkIfGroupContainsMax1NonDraftExperiment(List<String> experiments) {
        int numberOfNonDraftExperiments = experiments.stream()
                .map(experimentsRepository::getExperiment)
                .map(e -> !e.get().getStatus().equals(ExperimentStatus.DRAFT))
                .mapToInt(experimentIsNotDraft -> experimentIsNotDraft ? 1 : 0)
                .sum();

        if (numberOfNonDraftExperiments > 1) {
            throw new ExperimentCommandException("Cannot create group with more than 1 active experiment");
        }
    }

    private void checkIfGroupNameIsUnique(String id) {
        if (experimentGroupRepository.exists(id)) {
            throw new ExperimentCommandException("Provided group name is not unique");
        }
    }

    private void checkIfGroupContainsAtLeast2Experiments(List<String> experiments) {
        if (experiments.size() < 2) {
            throw new ExperimentCommandException("Cannot create group with less than 2 experiments");
        }
    }

    private void checkIfAllExperimentsInGroupExist(List<String> experiments) {
        boolean allExperimentsExist = experiments.stream()
                .map(experimentsRepository::getExperiment)
                .mapToInt(eo -> eo.map(e -> 1).orElse(0))
                .sum() == experiments.size();

        if (!allExperimentsExist) {
            throw new ExperimentCommandException("Cannot create group if not all experiments exist");
        }
    }

    private void checkIfNoExperimentIsFullOn(List<String> experiments) {
        boolean anyExperimentIsFullOn = experiments.stream()
                .map(experimentsRepository::getExperiment)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .anyMatch(Experiment::isFullOn);

        if (anyExperimentIsFullOn) {
            throw new ExperimentCommandException("Cannot create group if one of the experiments is full-on");
        }
    }

    private boolean enoughPercentageSpace(List<String> experiments) {
        return ExperimentGroup.enoughPercentageSpace(experiments.stream()
                .map(experimentsRepository::getExperiment)
                .map(e -> e.get().getDefinition().get())
                .collect(Collectors.toList())
        );
    }

    public String getNotificationMessage() {
        return "Experiments with ids '" + String.join(",", experimentGroupCreationRequest.getExperiments()) + "' was combined in group. ";
    }
}