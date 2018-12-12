package pl.allegro.experiments.chi.chiserver.infrastructure.scorer;

import pl.allegro.experiments.chi.chiserver.domain.scorer.OfferScore;
import pl.allegro.experiments.chi.chiserver.domain.scorer.OfferScoreRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MongoOfferScoreRepository implements OfferScoreRepository {
    private final RandomOfferScoreRepository randomOfferScoreRepository;
    private final OfferScoreCrudRepository offerScoreCrudRepository;

    public MongoOfferScoreRepository(
            RandomOfferScoreRepository randomOfferScoreRepository,
            OfferScoreCrudRepository offerScoreCrudRepository) {
        this.randomOfferScoreRepository = randomOfferScoreRepository;
        this.offerScoreCrudRepository = offerScoreCrudRepository;
    }

    @Override
    public List<OfferScore> scores() {
        Map<String, OfferScore> result = new HashMap<>();
        for (OfferScore offerScore: randomOfferScoreRepository.scores()) {
            result.put(offerScore.getOffer().getOfferId(), offerScore);
        }
        for (OfferScore offerScore: offerScoreCrudRepository.findAll()) {
            if (result.containsKey(offerScore.getOffer().getOfferId())) {
                result.put(offerScore.getOffer().getOfferId(), offerScore);
            }
        }
        return new ArrayList<>(result.values());
    }

    @Override
    public void updateScores(List<OfferScore> offerScores) {
        offerScoreCrudRepository.saveAll(offerScores);
    }
}
