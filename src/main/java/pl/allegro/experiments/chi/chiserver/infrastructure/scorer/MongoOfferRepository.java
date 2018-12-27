package pl.allegro.experiments.chi.chiserver.infrastructure.scorer;

import com.google.common.collect.ImmutableList;
import org.springframework.stereotype.Repository;
import pl.allegro.experiments.chi.chiserver.domain.scorer.Offer;
import pl.allegro.experiments.chi.chiserver.domain.scorer.OfferRepository;
import pl.allegro.experiments.chi.chiserver.domain.scorer.ToManyOffersException;

import java.util.List;
import java.util.Set;

@Repository
public class MongoOfferRepository implements OfferRepository {
    private final OfferCrudRepository offerCrudRepository;
    private final OfferScoreCrudRepository offerScoreCrudRepository;

    public MongoOfferRepository(
            OfferCrudRepository offerCrudRepository,
            OfferScoreCrudRepository offerScoreCrudRepository) {
        this.offerCrudRepository = offerCrudRepository;
        this.offerScoreCrudRepository = offerScoreCrudRepository;
    }

    @Override
    public void setOffers(Set<Offer> offers) {
        if (offers.size() > 200) {
            throw new ToManyOffersException();
        }
        offerCrudRepository.deleteAll();
        offerCrudRepository.saveAll(offers);
        offerScoreCrudRepository.deleteAll();
    }

    @Override
    public List<Offer> all() {
        return ImmutableList.copyOf(offerCrudRepository.findAll());
    }
}
