package pl.allegro.experiments.chi.chiserver.domain.experiments.administration.start;

public class StartExperimentProperties {
    private long experimentDurationDays;

    public StartExperimentProperties() {}

    public StartExperimentProperties(long experimentDurationDays) {
        this.experimentDurationDays = experimentDurationDays;
    }

    public long getExperimentDurationDays() {
        return experimentDurationDays;
    }

    public void setExperimentDurationDays(long experimentDurationDays) {
        this.experimentDurationDays = experimentDurationDays;
    }
}
