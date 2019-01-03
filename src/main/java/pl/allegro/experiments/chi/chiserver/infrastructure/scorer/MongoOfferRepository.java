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
    private final MongoExperimentOfferRepository experimentOfferRepository;

    public MongoOfferRepository(
            MongoExperimentOfferRepository experimentOfferRepository) {
        this.experimentOfferRepository = experimentOfferRepository;
    }

    @Override
    public void setOffers(Set<Offer> offers) {
        if (offers.size() > 200) {
            throw new ToManyOffersException();
        }
        ExperimentOffer offerExperiment = experimentOfferRepository.get();
        experimentOfferRepository.save(offerExperiment
                .withOffers(ImmutableList.copyOf(offers)));
    }

    @Override
    public List<Offer> all() {
        return ImmutableList.copyOf(experimentOfferRepository.get().getOffers());
    }
}
