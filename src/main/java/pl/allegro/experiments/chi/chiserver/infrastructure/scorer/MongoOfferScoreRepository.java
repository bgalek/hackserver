package pl.allegro.experiments.chi.chiserver.infrastructure.scorer;

import com.google.common.collect.ImmutableList;
import pl.allegro.experiments.chi.chiserver.domain.scorer.OfferScore;
import pl.allegro.experiments.chi.chiserver.domain.scorer.OfferScoreRepository;
import pl.allegro.experiments.chi.chiserver.domain.scorer.OfferScoreValueOutOfBoundsException;

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
        Map<String, OfferScore> result = new HashMap<>();
        for (OfferScore offerScore: randomOfferScoreRepository.scores()) {
            result.put(offerScore.getOffer().getOfferId(), offerScore);
        }
        for (OfferScore offerScore: offerScoreCrudRepository.findAll()) {
            if (result.containsKey(offerScore.getOffer().getOfferId())) {
                result.put(offerScore.getOffer().getOfferId(), offerScore);
            }
        }
        return ImmutableList.copyOf(result.values().stream()
                .sorted(Comparator.comparingDouble(it -> -it.getScore().getValue()))
                .collect(Collectors.toList()));
    }

    @Override
    public void setScores(List<OfferScore> offerScores) {
        if(!offerScores.stream().allMatch(it -> it.getScore().getValue() >= 0 && it.getScore().getValue() <= 1)) {
            throw new OfferScoreValueOutOfBoundsException();
        }
        offerScoreCrudRepository.saveAll(offerScores);
    }
}
