package pl.allegro.experiments.chi.chiserver.domain.experiments.administration.create;

import com.google.common.base.Preconditions;
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
        Preconditions.checkNotNull(experimentsRepository);
        Preconditions.checkNotNull(userProvider);
        this.experimentsRepository = experimentsRepository;
        this.userProvider = userProvider;
    }

    public CreateExperimentCommand createExperimentCommand(ExperimentCreationRequest request) {
        Preconditions.checkNotNull(request);
        return new CreateExperimentCommand(experimentsRepository, userProvider, request);
    }
}