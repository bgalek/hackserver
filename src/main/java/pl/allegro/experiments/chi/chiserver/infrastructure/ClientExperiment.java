package pl.allegro.experiments.chi.chiserver.infrastructure;

import pl.allegro.experiments.chi.chiserver.domain.experiments.ActivityPeriod;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentDefinition;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentStatus;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentVariant;

import java.util.List;

public class ClientExperiment {
    private final String id;
    private final List<ExperimentVariant> variants;
    private final ActivityPeriod activityPeriod;
    private final ExperimentStatus status;

    public ClientExperiment(ExperimentDefinition experiment) {
        id = experiment.getId();
        variants = experiment.prepareExperimentVariants();
        activityPeriod = experiment.getActivityPeriod();
        status = experiment.getStatus();
    }

    ClientExperiment(
            String id,
            List<ExperimentVariant> variants,
            ActivityPeriod activityPeriod,
            ExperimentStatus status) {
        this.id = id;
        this.variants = variants;
        this.activityPeriod = activityPeriod;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public List<ExperimentVariant> getVariants() {
        return variants;
    }

    public ActivityPeriod getActivityPeriod() {
        return activityPeriod;
    }

    public ExperimentStatus getStatus() {
        return status;
    }
}
