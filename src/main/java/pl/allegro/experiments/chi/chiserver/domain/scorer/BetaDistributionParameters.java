package pl.allegro.experiments.chi.chiserver.domain.scorer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class BetaDistributionParameters {
    private final int alpha;
    private final int beta;

    @JsonCreator
    BetaDistributionParameters(
            @JsonProperty("alpha") int alpha,
            @JsonProperty("beta") int beta) {
        this.alpha = alpha;
        this.beta = beta;
    }

    public int getAlpha() {
        return alpha;
    }

    public int getBeta() {
        return beta;
    }

    public static BetaDistributionParameters defaultParameters() {
        return new BetaDistributionParameters(1, 1);
    }
}
