package pl.allegro.experiments.chi.chiserver.infrastructure.scorer;

import com.google.common.collect.ImmutableList;
import pl.allegro.experiments.chi.chiserver.domain.scorer.OfferScore;
import pl.allegro.experiments.chi.chiserver.domain.scorer.OfferScoreRepository;

import java.util.*;
import java.util.stream.Collectors;

public class MongoOfferScoreRepository implements OfferScoreRepository {
    private final RandomOfferScoreRepository randomOfferScoreRepository;
    private final OfferScoreCrudRepository offerScoreCrudRepository;

    MongoOfferScoreRepository(
            RandomOfferScoreRepository randomOfferScoreRepository,
            OfferScoreCrudRepository offerScoreCrudRepository) {
        this.randomOfferScoreRepository = randomOfferScoreRepository;
        this.offerScoreCrudRepository = offerScoreCrudRepository;
    }

    @Override
    public List<OfferScore> scores() {
        Map<String, OfferScore> randomScoreDefaults = randomScoreDefaults();
        Map<String, OfferScore> result = applyCalculatedScores(randomScoreDefaults);

        return ImmutableList.copyOf(result.values().stream()
                .sorted(Comparator.comparingDouble(it -> -it.getScore().getValue()))
                .collect(Collectors.toList()));
    }

    private Map<String, OfferScore> randomScoreDefaults() {
        Map<String, OfferScore> defaults = new HashMap<>();
        for (OfferScore offerScore: randomOfferScoreRepository.scores()) {
            defaults.put(offerScore.getOffer().getOfferId(), offerScore);
        }
        return defaults;
    }

    private Map<String, OfferScore> applyCalculatedScores(Map<String, OfferScore> randomDefaults) {
        for (OfferScore offerScore: offerScoreCrudRepository.findAll()) {
            if (randomDefaults.containsKey(offerScore.getOffer().getOfferId())) {
                randomDefaults.put(
                        offerScore.getOffer().getOfferId(),
                        offerScore.plus(randomDefaults.get(offerScore.getOffer().getOfferId())));
            }
        }
        return randomDefaults;
    }

    @Override
    public void updateScores(List<OfferScore> offerScoreUpdate) {
        Map<String, OfferScore>  currentScores = ImmutableList.copyOf(offerScoreCrudRepository.findAll()).stream()
                .collect(Collectors.toMap(it -> it.getOffer().getOfferId(), it -> it));

        for (OfferScore offerScore: offerScoreUpdate) {
            String offerId = offerScore.getOffer().getOfferId();
            if (currentScores.containsKey(offerId)) {
                currentScores.put(offerId, currentScores.get(offerId).plus(offerScore));
            } else {
                currentScores.put(offerId, offerScore);
            }
        }
        offerScoreCrudRepository.deleteAll();
        offerScoreCrudRepository.saveAll(currentScores.values());
    }
}