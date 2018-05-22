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
        // legacy mongo experiments
        if (experimentGroupRepository.exists(id)) {
            throw new ExperimentCommandException("Provided group name is not unique");
        }

        if (experiments.size() < 2) {
            throw new ExperimentCommandException("Cannot create group with less than 2 experiments");
        }

        int experimentsFound = experiments.stream()
                .map(experimentsRepository::getExperiment)
                .mapToInt(eo -> eo.map(e -> 1).orElse(0))
                .sum();

        if (experimentsFound != experiments.size()) {
            throw new ExperimentCommandException("Cannot create group if not all experiments exist");
        }

        int numberOfStartedExperiments = experiments.stream()
                .map(experimentsRepository::getExperiment)
                .map(e -> !e.get().getStatus().equals(ExperimentStatus.DRAFT))
                .mapToInt(experimentIsNotDraft -> experimentIsNotDraft ? 1 : 0)
                .sum();

        if (numberOfStartedExperiments > 1) {
            throw new ExperimentCommandException("Cannot create group with more than 1 active experiment");
        }

        int numberOfStashExperiments = experiments.stream()
                .map(experimentsRepository::getExperiment)
                .map(e -> experimentsRepository.getOrigin(e.get().getId()))
                .mapToInt(origin -> origin.equals(ExperimentOrigin.STASH) ? 1 : 0)
                .sum();

        if (numberOfStashExperiments != 0) {
            throw new ExperimentCommandException("Cannot create group with stash experiments");
        }

        int numberOfBackendExperiments = experiments.stream()
                .map(experimentsRepository::getExperiment)
                .map(e -> e.get())
                .map(e -> e.getReportingDefinition().getType())
                .mapToInt(reportingType -> reportingType.equals(ReportingType.BACKEND) ? 1 : 0)
                .sum();

        if (numberOfBackendExperiments != experiments.size()) {
            throw new ExperimentCommandException("Cannot create group if one of the experiments is not BACKEND");
        }

        if (!enoughPercentageSpace(experiments)) {
            throw new ExperimentCommandException("Cannot create group there is no enough percentage space");
        }

        if (experiments.stream().anyMatch(e -> experimentGroupRepository.experimentHasGroup(e))) {
            throw new ExperimentCommandException("Cannot create group is one of the experiments is in another group");
        }

        ExperimentGroup experimentGroup = new ExperimentGroup(id, UUID.randomUUID().toString(), experiments);
        experimentGroupRepository.save(experimentGroup);
        return experimentGroup;
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