package pl.allegro.experiments.chi.chiserver.infrastructure.scorer;

import com.google.common.collect.ImmutableList;
import org.springframework.stereotype.Repository;
import pl.allegro.experiments.chi.chiserver.domain.scorer.Offer;
import pl.allegro.experiments.chi.chiserver.domain.scorer.OfferParameters;
import pl.allegro.experiments.chi.chiserver.domain.scorer.OfferParametersRepository;
import pl.allegro.experiments.chi.chiserver.domain.scorer.Parameters;

import java.util.*;

@Repository
public class MongoOfferParametersRepository implements OfferParametersRepository {
    private final MongoScorerContainerRepository scorerContainerRepository;

    public MongoOfferParametersRepository(MongoScorerContainerRepository scorerContainerRepository) {
        this.scorerContainerRepository = scorerContainerRepository;
    }

    @Override
    public List<OfferParameters> all() {
        ScorerContainer scorerContainer = scorerContainerRepository.get();
        List<Offer> offers = scorerContainer.getOffers();
        Map<Offer, OfferParameters> result = new HashMap<>();
        for (Offer offer: offers) {
            result.put(offer, new OfferParameters(offer, Parameters.defaultParameters()));
        }
        for (OfferParameters override: scorerContainer.getOffersParameters()) {
            if (result.containsKey(override.getOffer())) {
                result.put(override.getOffer(), override);
            }
        }
        return ImmutableList.copyOf(result.values());
    }

    @Override
    public void update(List<OfferParameters> offersParameters) {
        scorerContainerRepository.save(
                scorerContainerRepository.get()
                        .withOffersParameters(offersParameters));
    }
}