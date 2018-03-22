package pl.allegro.experiments.chi.chiserver.domain.experiments.administration;

import pl.allegro.experiments.chi.chiserver.domain.User;
import pl.allegro.experiments.chi.chiserver.domain.UserProvider;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository;
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.create.ExperimentCreationRequest;

import java.util.Objects;

public class CreateExperimentCommand {
    private final ExperimentsRepository experimentsRepository;
    private final UserProvider userProvider;
    private final ExperimentCreationRequest experimentCreationRequest;

    CreateExperimentCommand(
            ExperimentsRepository experimentsRepository,
            UserProvider userProvider,
            ExperimentCreationRequest experimentCreationRequest) {
        Objects.requireNonNull(experimentsRepository);
        Objects.requireNonNull(userProvider);
        Objects.requireNonNull(experimentCreationRequest);
        this.experimentsRepository = experimentsRepository;
        this.userProvider = userProvider;
        this.experimentCreationRequest = experimentCreationRequest;
    }

    public void execute() {
        User user = userProvider.getCurrentUser();
        if (user.isAnonymous() && !user.isRoot()) {
            throw new AuthorizationException("Only logged in user can create experiment");
        }
        if (experimentsRepository.getExperiment(experimentCreationRequest.getId()) != null) {
            throw new ExperimentCommandException("Experiment with id " + experimentCreationRequest.getId() + " already exists");
        }
        experimentsRepository.save(experimentCreationRequest.toExperiment(user.getName()));
    }
}
