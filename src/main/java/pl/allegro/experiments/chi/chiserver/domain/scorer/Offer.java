package pl.allegro.experiments.chi.chiserver.domain.scorer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;

import java.util.Objects;

public class Offer {

    private final String offerId;

    @JsonCreator
    private Offer(
            @JsonProperty("offerId") String offerId) {
        Preconditions.checkArgument(offerId != null, "null offerId");
        this.offerId = offerId;
    }

    public String getOfferId() {
        return offerId;
    }

    public static Offer of(String offerId) {
        return new Offer(offerId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Offer offer = (Offer) o;
        return offerId.equals(offer.offerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(offerId);
    }
}
