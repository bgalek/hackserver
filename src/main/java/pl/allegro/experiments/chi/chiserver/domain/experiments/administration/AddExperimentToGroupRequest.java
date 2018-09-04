package pl.allegro.experiments.chi.chiserver.domain.experiments.administration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;
import java.util.List;

public class AddExperimentToGroupRequest {

    @NotNull
    private final String id;

    @NotNull
    private final String experimentId;

    @JsonCreator
    AddExperimentToGroupRequest(
            @JsonProperty("id") String id,
            @JsonProperty("experiment") String experimentId) {
        this.id = id;
        this.experimentId = experimentId;
    }

    public String getId() {
        return id;
    }

    public String getExperimentId() {
        return experimentId;
    }
}
