package pl.allegro.experiments.chi.chiserver.domain.scorer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;

public class OfferParameters {
    private final Offer offer;
    private final Parameters parameters;

    @JsonCreator
    public OfferParameters(
            @JsonProperty("offer") Offer offer,
            @JsonProperty("parameters") Parameters parameters) {
        Preconditions.checkNotNull(offer);
        Preconditions.checkNotNull(parameters);
        this.offer = offer;
        this.parameters = parameters;
    }

    public Offer getOffer() {
        return offer;
    }

    public Parameters getParameters() {
        return parameters;
    }
}
