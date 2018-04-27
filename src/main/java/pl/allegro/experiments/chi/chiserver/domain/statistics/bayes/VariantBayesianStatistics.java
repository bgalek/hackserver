package pl.allegro.experiments.chi.chiserver.domain.statistics.bayes;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class VariantBayesianStatistics {
    private final String variantName;
    private final Samples samples;

    @JsonCreator
    public VariantBayesianStatistics(@JsonProperty("variantName") String variantName,
                                     @JsonProperty("samples") Samples samples) {
        this.variantName = variantName;
        this.samples = samples;
    }

    public String getVariantName() {
        return variantName;
    }

    public Samples getSamples() {
        return samples;
    }
}
