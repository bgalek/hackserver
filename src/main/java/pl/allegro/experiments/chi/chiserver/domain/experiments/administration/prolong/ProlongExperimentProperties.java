package pl.allegro.experiments.chi.chiserver.domain.experiments.administration.prolong;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;

import javax.validation.constraints.NotNull;

public class ProlongExperimentProperties {
    @NotNull
    private long experimentAdditionalDays;

    @JsonCreator
    public ProlongExperimentProperties(@JsonProperty("experimentAdditionalDays") long experimentAdditionalDays) {
        Preconditions.checkArgument(experimentAdditionalDays > 0);
        this.experimentAdditionalDays = experimentAdditionalDays;
    }

    long getExperimentAdditionalDays() {
        return experimentAdditionalDays;
    }
}