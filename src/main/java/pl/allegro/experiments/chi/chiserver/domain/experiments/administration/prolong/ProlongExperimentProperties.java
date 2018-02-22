package pl.allegro.experiments.chi.chiserver.domain.experiments.administration.prolong;

public class ProlongExperimentProperties {
    private long experimentAdditionalDays;

    public ProlongExperimentProperties() {}

    public ProlongExperimentProperties(long experimentAdditionalDays) {
        this.experimentAdditionalDays = experimentAdditionalDays;
    }

    public long getExperimentAdditionalDays() {
        return experimentAdditionalDays;
    }
}