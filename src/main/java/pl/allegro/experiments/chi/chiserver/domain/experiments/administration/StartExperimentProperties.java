package pl.allegro.experiments.chi.chiserver.domain.experiments.administration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;

import javax.validation.constraints.NotNull;

public class StartExperimentProperties {
    @NotNull
    private long experimentDurationDays;

    @JsonCreator
    public StartExperimentProperties(@JsonProperty("experimentDurationDays") long experimentDurationDays) {
        Preconditions.checkArgument(experimentDurationDays > 0);
        this.experimentDurationDays = experimentDurationDays;
    }

    long getExperimentDurationDays() {
        return experimentDurationDays;
    }

}
