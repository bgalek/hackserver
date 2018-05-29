package pl.allegro.experiments.chi.chiserver.domain.experiments.administration;

import pl.allegro.experiments.chi.chiserver.domain.User;
import pl.allegro.experiments.chi.chiserver.domain.UserProvider;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentStatus;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ReportingType;
import pl.allegro.experiments.chi.chiserver.domain.experiments.groups.ExperimentGroup;
import pl.allegro.experiments.chi.chiserver.domain.experiments.groups.ExperimentGroupRepository;
import pl.allegro.experiments.chi.chiserver.infrastructure.experiments.ExperimentOrigin;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class CreateExperimentGroupCommand {
    private final ExperimentGroupRepository experimentGroupRepository;
    private final ExperimentsRepository experimentsRepository;
    private final UserProvider userProvider;
    private final ExperimentGroupCreationRequest experimentGroupCreationRequest;

    CreateExperimentGroupCommand(
            ExperimentGroupRepository experimentGroupRepository,
            ExperimentsRepository experimentsRepository,
            UserProvider userProvider,
            ExperimentGroupCreationRequest experimentGroupCreationRequest) {
        Objects.requireNonNull(experimentGroupRepository);
        Objects.requireNonNull(userProvider);
        Objects.requireNonNull(experimentsRepository);
        Objects.requireNonNull(experimentGroupCreationRequest);
        this.experimentGroupRepository = experimentGroupRepository;
        this.userProvider = userProvider;
        this.experimentsRepository = experimentsRepository;
        this.experimentGroupCreationRequest = experimentGroupCreationRequest;
    }

    public void execute() {
        User user = userProvider.getCurrentUser();
        if (user.isAnonymous() && !user.isRoot()) {
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

        checkIfAllExperimentsInGroupAreBackend(experiments);

        checkIfGroupHasEnoughPercentageSpaceForAllExperiments(experiments);

        checkIfAllExperimentsHaveNoGroup(experiments);

        return new ExperimentGroup(id, getNameSpace(experiments), experiments);
    }

    private String getNameSpace(List<String> experiments) {
        return experiments.stream()
                .map(experimentsRepository::getExperiment)
                .filter(experiment ->
                    experiment.isPresent() &&
                            !experiment.get().getStatus().equals(ExperimentStatus.DRAFT)
                ).map(experiment -> experiment.get())
                .findFirst()
                .map(experiment -> experiment.getId())
                .orElse(UUID.randomUUID().toString());
    }

    private void checkIfAllExperimentsHaveNoGroup(List<String> experiments) {
        if (experiments.stream().anyMatch(e -> experimentGroupRepository.experimentInGroup(e))) {
            throw new ExperimentCommandException("Cannot create group is one of the experiments is in another group");
        }
    }

    private void checkIfGroupHasEnoughPercentageSpaceForAllExperiments(List<String> experiments) {
        if (!enoughPercentageSpace(experiments)) {
            throw new ExperimentCommandException("Cannot create group there is no enough percentage space");
        }
    }

    private void checkIfAllExperimentsInGroupAreBackend(List<String> experiments) {
        int numberOfBackendExperiments = experiments.stream()
                .map(experimentsRepository::getExperiment)
                .map(e -> e.get())
                .map(e -> e.getReportingDefinition().getType())
                .mapToInt(reportingType -> reportingType.equals(ReportingType.BACKEND) ? 1 : 0)
                .sum();

        if (numberOfBackendExperiments != experiments.size()) {
            throw new ExperimentCommandException("Cannot create group if one of the experiments is not BACKEND");
        }
    }

    private void checkIfGroupDoesNotContainsStashExperiments(List<String> experiments) {
        int numberOfStashExperiments = experiments.stream()
                .map(experimentsRepository::getExperiment)
                .map(e -> experimentsRepository.getOrigin(e.get().getId()))
                .mapToInt(origin -> origin.equals(ExperimentOrigin.STASH) ? 1 : 0)
                .sum();

        if (numberOfStashExperiments != 0) {
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

    private boolean enoughPercentageSpace(List<String> experiments) {
        int maxBasePercentage = experiments.stream()
                .map(experimentsRepository::getExperiment)
                .map(e -> e.get())
                .map(e -> e.getDefinition()
                        .get()
                        .getPercentage()
                        .get())
                .max(Integer::compare)
                .get();
        int percentageSum = experiments.stream()
                .map(experimentsRepository::getExperiment)
                .map(e -> e.get())
                .mapToInt(e -> {
                    int percentage = e.getDefinition()
                            .get()
                            .getPercentage()
                            .get();
                    int numberOfVariantsDifferentThanBase = e.getDefinition()
                            .get()
                            .getVariantNames()
                            .size() - 1;
                    return numberOfVariantsDifferentThanBase * percentage;
                }).sum();

        return maxBasePercentage + percentageSum <= 100;
    }
}