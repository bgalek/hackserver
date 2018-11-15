package pl.allegro.experiments.chi.chiserver.domain.experiments.administration;

import pl.allegro.experiments.chi.chiserver.domain.User;
import pl.allegro.experiments.chi.chiserver.domain.UserProvider;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentTagRepository;

import java.util.Objects;

public class CreateExperimentTagCommand implements Command {

    private final ExperimentTagRepository experimentTagRepository;
    private final UserProvider userProvider;
    private final ExperimentTagCreationRequest experimentTagCreationRequest;

    CreateExperimentTagCommand(
            ExperimentTagRepository experimentTagRepository,
            UserProvider userProvider,
            ExperimentTagCreationRequest experimentTagCreationRequest) {
        Objects.requireNonNull(experimentTagRepository);
        Objects.requireNonNull(userProvider);
        Objects.requireNonNull(experimentTagCreationRequest);
        this.experimentTagRepository = experimentTagRepository;
        this.userProvider = userProvider;
        this.experimentTagCreationRequest = experimentTagCreationRequest;
    }

    @Override
    public void execute() {
        User user = userProvider.getCurrentUser();
        if (!user.isRoot()) {
            throw new AuthorizationException("Only administrator can create tags");
        }
        if (experimentTagRepository.get(experimentTagCreationRequest.getExperimentTagId()).isPresent()) {
            throw new ExperimentCommandException("Experiment tag " + experimentTagCreationRequest.getExperimentTagId() + " already exists");
        }
        if (experimentTagCreationRequest.getExperimentTagId().length() > 15) {
            throw new ExperimentCommandException("Maximum experiment tag length is 15");
        }
        experimentTagRepository.save(experimentTagCreationRequest.toExperimentTag());
    }
}
