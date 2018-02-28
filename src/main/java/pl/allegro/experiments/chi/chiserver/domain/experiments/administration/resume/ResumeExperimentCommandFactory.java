package pl.allegro.experiments.chi.chiserver.domain.experiments.administration.resume;

import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository;
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.PermissionsAwareExperimentRepository;

import java.util.Objects;

public class ResumeExperimentCommandFactory {
    private final ExperimentsRepository experimentsRepository;
    private final PermissionsAwareExperimentRepository permissionsAwareExperimentRepository;

    public ResumeExperimentCommandFactory(
            ExperimentsRepository experimentsRepository,
            PermissionsAwareExperimentRepository permissionsAwareExperimentRepository) {
        Objects.requireNonNull(experimentsRepository);
        Objects.requireNonNull(permissionsAwareExperimentRepository);
        this.experimentsRepository = experimentsRepository;
        this.permissionsAwareExperimentRepository = permissionsAwareExperimentRepository;
    }

    public ResumeExperimentCommand resumeExperimentCommand(String experimentId) {
        return new ResumeExperimentCommand(
                experimentId,
                experimentsRepository,
                permissionsAwareExperimentRepository
        );
    }
}
