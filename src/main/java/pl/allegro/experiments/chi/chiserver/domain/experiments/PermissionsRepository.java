package pl.allegro.experiments.chi.chiserver.domain.experiments;

import com.google.common.base.Preconditions;
import pl.allegro.experiments.chi.chiserver.domain.User;
import pl.allegro.experiments.chi.chiserver.domain.UserProvider;

public class PermissionsRepository {
    private final UserProvider userProvider;
    private final ExperimentsRepository experimentsRepository;

    public PermissionsRepository(
            UserProvider userProvider,
            ExperimentsRepository experimentsRepository) {
        Preconditions.checkNotNull(userProvider);
        Preconditions.checkNotNull(experimentsRepository);
        this.userProvider = userProvider;
        this.experimentsRepository = experimentsRepository;
    }

    public Experiment withPermissions(Experiment experiment) {
        User currentUser = userProvider.getCurrentUser();
        return experiment.withEditableFlag(currentUser.isOwner(experiment))
                .withOrigin(experimentsRepository.getOrigin(experiment.getId()));
    }
}
