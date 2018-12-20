package pl.allegro.experiments.chi.chiserver.domain.scorer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;

import java.util.Objects;

public class OfferScore {
    private final Offer offer;
    private final Score score;

    @JsonCreator
    private OfferScore(
            @JsonProperty("offer") Offer offer,
            @JsonProperty("score") Score score) {
        Preconditions.checkArgument(offer != null, "null offer in OfferScore");
        Preconditions.checkArgument(score != null, "null score in OfferScore");
        this.offer = offer;
        this.score = score;
    }

    public Offer getOffer() {
        return offer;
    }

    public Score getScore() {
        return score;
    }

    public static OfferScore of(Offer offer, Score score) {
        return new OfferScore(offer, score);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OfferScore that = (OfferScore) o;
        return offer.equals(that.offer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(offer);
    }
}
