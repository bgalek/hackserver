package pl.allegro.experiments.chi.chiserver.domain.experiments.administration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

public class PairedExperimentCreationRequest {

    @NotNull
    private final ExperimentCreationRequest experimentCreationRequest;

    @NotNull
    private final AddExperimentToGroupRequest addExperimentToGroupRequest;

    @JsonCreator
    public PairedExperimentCreationRequest(
            @JsonProperty("experimentCreationRequest") ExperimentCreationRequest experimentCreationRequest,
            @JsonProperty("addExperimentToGroupRequest") AddExperimentToGroupRequest addExperimentToGroupRequest) {
        this.experimentCreationRequest = experimentCreationRequest;
        this.addExperimentToGroupRequest = addExperimentToGroupRequest;
    }

    public ExperimentCreationRequest getExperimentCreationRequest() {
        return experimentCreationRequest;
    }

    public AddExperimentToGroupRequest getAddExperimentToGroupRequest() {
        return addExperimentToGroupRequest;
    }
}
