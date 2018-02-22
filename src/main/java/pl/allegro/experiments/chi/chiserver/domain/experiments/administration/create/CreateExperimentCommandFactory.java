package pl.allegro.experiments.chi.chiserver.domain.experiments.administration.create;

import org.springframework.stereotype.Component;
import pl.allegro.experiments.chi.chiserver.domain.UserProvider;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository;

@Component
public class CreateExperimentCommandFactory {
    private final ExperimentsRepository experimentsRepository;
    private final UserProvider userProvider;

    public CreateExperimentCommandFactory(
            ExperimentsRepository experimentsRepository,
            UserProvider userProvider) {
        this.experimentsRepository = experimentsRepository;
        this.userProvider = userProvider;
    }

    public CreateExperimentCommand createExperimentCommand(ExperimentCreationRequest request) {
        return new CreateExperimentCommand(experimentsRepository, userProvider, request);
    }
}