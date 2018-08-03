package pl.allegro.experiments.chi.chiserver.domain.experiments.administration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

public class MakeExperimentFullOnProperties {
    @NotNull
    private final String variantName;

    @JsonCreator
    public MakeExperimentFullOnProperties(@JsonProperty("variantName") String variantName) {
        this.variantName = variantName;
    }

    public String getVariantName() {
        return variantName;
    }
}
