package pl.allegro.experiments.chi.chiserver.domain.experiments.administration;

import pl.allegro.experiments.chi.chiserver.domain.User;
import pl.allegro.experiments.chi.chiserver.domain.UserProvider;
import pl.allegro.experiments.chi.chiserver.domain.experiments.Experiment;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository;

public class PermissionsAwareExperimentGetter {
    private final ExperimentsRepository experimentsRepository;
    private final UserProvider userProvider;

    public PermissionsAwareExperimentGetter(
            ExperimentsRepository experimentsRepository,
            UserProvider userProvider) {
        this.experimentsRepository = experimentsRepository;
        this.userProvider = userProvider;
    }

    public Experiment getExperimentOrException(String experimentId) {
        Experiment experiment = experimentsRepository.getExperiment(experimentId);
        if (experiment == null) {
            throw new ExperimentNotFoundException("Experiment not found: " + experimentId);
        }

        User user = userProvider.getCurrentUser();
        if (!user.isOwner(experiment)) {
            throw new AuthorizationException("User has no permission to edit experiment: " + experimentId);
        }

        return experiment;
    }
}
