package pl.allegro.experiments.chi.chiserver.domain.experiments.administration;

import pl.allegro.experiments.chi.chiserver.domain.User;
import pl.allegro.experiments.chi.chiserver.domain.UserProvider;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository;
import pl.allegro.experiments.chi.chiserver.domain.experiments.groups.ExperimentGroup;
import pl.allegro.experiments.chi.chiserver.domain.experiments.groups.ExperimentGroupRepository;

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
        ExperimentGroup experimentGroup = new ExperimentGroup(id, UUID.randomUUID().toString(), experiments);
        experimentGroupRepository.save(experimentGroup);
        return experimentGroup;
    }
}