package pl.allegro.experiments.chi.chiserver.domain.experiments.administration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;
import java.util.List;

public class ExperimentGroupCreationRequest {

    @NotNull
    private final String id;

    @NotNull
    private final List<String> experiments;

    @JsonCreator
    ExperimentGroupCreationRequest(
            @JsonProperty("id") String id,
            @JsonProperty("experiments") List<String> experiments) {
        this.id = id;
        this.experiments = experiments;
    }

    public String getId() {
        return id;
    }

    public List<String> getExperiments() {
        return experiments;
    }
}
