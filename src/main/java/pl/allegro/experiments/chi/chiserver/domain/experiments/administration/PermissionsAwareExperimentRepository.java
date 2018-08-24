package pl.allegro.experiments.chi.chiserver.domain.experiments.administration;

import com.google.common.base.Preconditions;
import pl.allegro.experiments.chi.chiserver.domain.UserProvider;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentDefinition;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class PermissionsAwareExperimentRepository {
    private final ExperimentsRepository experimentsRepository;
    private final UserProvider userProvider;

    public PermissionsAwareExperimentRepository(
            ExperimentsRepository experimentsRepository,
            UserProvider userProvider) {
        Preconditions.checkNotNull(experimentsRepository);
        Preconditions.checkNotNull(userProvider);
        this.experimentsRepository = experimentsRepository;
        this.userProvider = userProvider;
    }

    public ExperimentDefinition getExperimentOrException(String experimentId) {
        Preconditions.checkNotNull(experimentId);
        ExperimentDefinition experiment = experimentsRepository.getExperiment(experimentId)
                .orElseThrow(() -> new ExperimentNotFoundException("Experiment not found: " + experimentId));
        if (!userProvider.getCurrentUser().isOwner(experiment)) {
            throw new AuthorizationException("User has no permission to edit experiment: " + experimentId);
        }
        return experiment;
    }
}