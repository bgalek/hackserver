package pl.allegro.experiments.chi.chiserver.domain.experiments.administration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentTag;

import javax.validation.constraints.NotNull;

public class ExperimentTagCreationRequest {
    @NotNull
    private final String experimentTagId;

    @JsonCreator
    public ExperimentTagCreationRequest(
            @JsonProperty("experimentTagId") String experimentTagId) {
        this.experimentTagId = experimentTagId;
    }

    public String getExperimentTagId() {
        return experimentTagId;
    }

    public ExperimentTag toExperimentTag() {
        return new ExperimentTag(experimentTagId);
    }
}
