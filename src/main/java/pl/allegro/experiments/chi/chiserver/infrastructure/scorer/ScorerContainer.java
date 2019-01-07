package pl.allegro.experiments.chi.chiserver.infrastructure.scorer;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import pl.allegro.experiments.chi.chiserver.domain.scorer.Offer;
import pl.allegro.experiments.chi.chiserver.domain.scorer.OfferParameters;

import java.util.*;

@Document(collection = "scorerContainer")
public class ScorerContainer {

    static final String ID = "contentAutomationPoc";

    @org.springframework.data.annotation.Id
    private final String id;
    private final List<Offer> offers;
    private final List<OfferParameters> offersParameters;

    @PersistenceConstructor
    ScorerContainer(
            String id,
            List<Offer> offers,
            List<OfferParameters> offersParameters) {
        Preconditions.checkNotNull(id);
        Preconditions.checkNotNull(offers);
        Preconditions.checkNotNull(offersParameters);
        this.id = id;
        this.offers = ImmutableList.copyOf(offers);
        this.offersParameters = ImmutableList.copyOf(offersParameters);
    }

    public String getId() {
        return id;
    }

    public List<Offer> getOffers() {
        return offers;
    }

    public List<OfferParameters> getOffersParameters() {
        return offersParameters;
    }

    ScorerContainer withOffers(List<Offer> newOffers) {
        return new ScorerContainer(id, newOffers, offersParameters);
    }

    ScorerContainer withOffersParameters(List<OfferParameters> newOffersParameters) {
        return new ScorerContainer(id, offers, newOffersParameters);
    }
}
