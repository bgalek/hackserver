package pl.allegro.experiments.chi.chiserver.domain.scorer;

import java.util.List;
import java.util.Set;

public interface OfferRepository {
    void setOffers(Set<Offer> offers);

    List<Offer> all();
}