package pl.allegro.experiments.chi.chiserver.infrastructure;

import pl.allegro.experiments.chi.chiserver.domain.experiments.ActivityPeriod;
import pl.allegro.experiments.chi.chiserver.domain.experiments.Experiment;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentStatus;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentVariant;

import java.util.List;

public class ClientExperiment {
    private final String id;
    private final List<ExperimentVariant> variants;
    private final boolean reportingEnabled;
    private final ActivityPeriod activityPeriod;
    private final ExperimentStatus status;

    public ClientExperiment(Experiment experiment) {
        id = experiment.getId();
        variants = experiment.getVariants();
        reportingEnabled = experiment.getReportingEnabled();
        activityPeriod = experiment.getActivityPeriod();
        status = experiment.getStatus();
    }

    ClientExperiment(
            String id,
            List<ExperimentVariant> variants,
            boolean reportingEnabled,
            ActivityPeriod activityPeriod,
            ExperimentStatus status) {
        this.id = id;
        this.variants = variants;
        this.reportingEnabled = reportingEnabled;
        this.activityPeriod = activityPeriod;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public List<ExperimentVariant> getVariants() {
        return variants;
    }

    public boolean isReportingEnabled() {
        return reportingEnabled;
    }

    public ActivityPeriod getActivityPeriod() {
        return activityPeriod;
    }

    public ExperimentStatus getStatus() {
        return status;
    }
}
