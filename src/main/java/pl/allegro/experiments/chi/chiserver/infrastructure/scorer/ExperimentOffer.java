package pl.allegro.experiments.chi.chiserver.infrastructure.scorer;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import pl.allegro.experiments.chi.chiserver.domain.scorer.Offer;
import pl.allegro.experiments.chi.chiserver.domain.scorer.OfferScore;

import java.util.List;

@Document(collection = "experimentOffer")
public class ExperimentOffer {
    static final String ID = "contentAutomationPoc";

    @org.springframework.data.annotation.Id
    private final String id;
    private final List<Offer> offers;
    private final List<OfferScore> offerScores;

    @PersistenceConstructor
    ExperimentOffer(
            String id,
            List<Offer> offers,
            List<OfferScore> offerScores) {
        Preconditions.checkArgument(id != null, "null ExperimentOffer.id");
        Preconditions.checkArgument(offers != null, "null ExperimentOffer.offers");
        Preconditions.checkArgument(offerScores != null, "null ExperimentOffer.offerScores");
        this.id = id;
        this.offers = ImmutableList.copyOf(offers);
        this.offerScores = ImmutableList.copyOf(offerScores);
    }

    public String getId() {
        return id;
    }

    public List<Offer> getOffers() {
        return offers;
    }

    public List<OfferScore> getOfferScores() {
        return offerScores;
    }

    ExperimentOffer withOffers(List<Offer> newOffers) {
        return new ExperimentOffer(id, newOffers, offerScores);
    }

    ExperimentOffer withOfferScores(List<OfferScore> newOfferScores) {
        return new ExperimentOffer(id, offers, newOfferScores);
    }
}
