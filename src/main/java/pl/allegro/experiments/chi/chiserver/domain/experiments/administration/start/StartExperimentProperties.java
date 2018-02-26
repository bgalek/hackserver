package pl.allegro.experiments.chi.chiserver.domain.experiments.administration.start;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

public class StartExperimentProperties {
    @NotNull
    private long experimentDurationDays;

    @JsonCreator
    public StartExperimentProperties(@JsonProperty("experimentDurationDays") long experimentDurationDays) {
        this.experimentDurationDays = experimentDurationDays;
    }

    long getExperimentDurationDays() {
        return experimentDurationDays;
    }

}
