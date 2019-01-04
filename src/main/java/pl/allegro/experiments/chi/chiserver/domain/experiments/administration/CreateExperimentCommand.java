package pl.allegro.experiments.chi.chiserver.domain.experiments.administration;

import pl.allegro.experiments.chi.chiserver.domain.User;
import pl.allegro.experiments.chi.chiserver.domain.UserProvider;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentTagRepository;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository;

import java.util.List;
import java.util.Objects;

public class CreateExperimentCommand implements ExperimentCommand {
    private final ExperimentsRepository experimentRepository;
    private final ExperimentTagRepository experimentTagRepository;
    private final UserProvider userProvider;
    private final ExperimentCreationRequest experimentCreationRequest;

    CreateExperimentCommand(
            ExperimentsRepository experimentRepository,
            ExperimentTagRepository experimentTagRepository,
            UserProvider userProvider,
            ExperimentCreationRequest experimentCreationRequest) {
        Objects.requireNonNull(experimentRepository);
        Objects.requireNonNull(experimentTagRepository);
        Objects.requireNonNull(userProvider);
        Objects.requireNonNull(experimentCreationRequest);
        this.experimentRepository = experimentRepository;
        this.experimentTagRepository = experimentTagRepository;
        this.userProvider = userProvider;
        this.experimentCreationRequest = experimentCreationRequest;
    }

    public void execute() {
        User user = userProvider.getCurrentUser();

        if (user.isAnonymous() && !user.isRoot()) {
            throw new AuthorizationException("Only logged in user can create experiment");
        }
        if (experimentRepository.getExperiment(experimentCreationRequest.getId()).isPresent()) {
            throw new ExperimentCommandException("Experiment with id " + experimentCreationRequest.getId() + " already exists");
        }
        if (!experimentTagRepository.tagsExist(experimentCreationRequest.getTags())) {
            throw new ExperimentCommandException("Tag does not exist");
        }

        experimentRepository.save(experimentCreationRequest.toExperimentDefinition(user.getName()));
    }

    @Override
    public String getNotificationMessage() {
        return "draft was created";
    }

    @Override
    public String getExperimentId() {
        return experimentCreationRequest.getId();
    }
}