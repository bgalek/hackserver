package pl.allegro.experiments.chi.chiserver.domain.scorer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Parameters {
    private final int alpha;
    private final int beta;

    @JsonCreator
    public Parameters(
            @JsonProperty("alpha") int alpha,
            @JsonProperty("beta") int beta) {
        this.alpha = alpha;
        this.beta = beta;
    }

    public float getAlpha() {
        return alpha;
    }

    public float getBeta() {
        return beta;
    }

    public static Parameters defaultParameters() {
        return new Parameters(1, 1);
    }
}
