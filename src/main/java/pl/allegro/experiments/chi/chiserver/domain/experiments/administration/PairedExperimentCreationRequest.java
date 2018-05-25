package pl.allegro.experiments.chi.chiserver.domain.experiments.administration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

public class PairedExperimentCreationRequest {

    @NotNull
    private final ExperimentCreationRequest experimentCreationRequest;

    @NotNull
    private final ExperimentGroupCreationRequest experimentGroupCreationRequest;

    @JsonCreator
    public PairedExperimentCreationRequest(
            @JsonProperty("experimentCreationRequest") ExperimentCreationRequest experimentCreationRequest,
            @JsonProperty("experimentGroupCreationRequest") ExperimentGroupCreationRequest experimentGroupCreationRequest) {
        this.experimentCreationRequest = experimentCreationRequest;
        this.experimentGroupCreationRequest = experimentGroupCreationRequest;
    }

    public ExperimentCreationRequest getExperimentCreationRequest() {
        return experimentCreationRequest;
    }

    public ExperimentGroupCreationRequest getExperimentGroupCreationRequest() {
        return experimentGroupCreationRequest;
    }
}
