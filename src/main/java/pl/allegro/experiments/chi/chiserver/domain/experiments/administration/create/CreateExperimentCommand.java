package pl.allegro.experiments.chi.chiserver.domain.experiments.administration.create;

import com.google.common.base.Preconditions;
import pl.allegro.experiments.chi.chiserver.domain.User;
import pl.allegro.experiments.chi.chiserver.domain.UserProvider;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository;
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.AuthorizationException;

public class CreateExperimentCommand {
    private final ExperimentsRepository experimentsRepository;
    private final UserProvider userProvider;
    private final ExperimentCreationRequest experimentCreationRequest;

    public CreateExperimentCommand(
            ExperimentsRepository experimentsRepository,
            UserProvider userProvider,
            ExperimentCreationRequest experimentCreationRequest) {
        Preconditions.checkNotNull(experimentsRepository);
        Preconditions.checkNotNull(userProvider);
        Preconditions.checkNotNull(experimentCreationRequest);
        this.experimentsRepository = experimentsRepository;
        this.userProvider = userProvider;
        this.experimentCreationRequest = experimentCreationRequest;
    }

    public void execute() {
        User user = userProvider.getCurrentUser();
        if (user.isAnonymous() && !user.isRoot()) {
            throw new AuthorizationException("Experiment with id " + experimentCreationRequest.getId() + " already exists");
        }
        if (experimentsRepository.getExperiment(experimentCreationRequest.getId()) != null) {
            throw new ExperimentCreationException("Experiment with id " + experimentCreationRequest.getId() + " already exists");
        }
        experimentsRepository.save(experimentCreationRequest.toExperiment(user.getName()));
    }
}
