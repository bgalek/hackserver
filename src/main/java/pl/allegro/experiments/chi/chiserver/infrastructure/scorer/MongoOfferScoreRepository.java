package pl.allegro.experiments.chi.chiserver.infrastructure.scorer;

import com.google.common.collect.ImmutableList;
import org.springframework.stereotype.Repository;
import pl.allegro.experiments.chi.chiserver.domain.scorer.Offer;
import pl.allegro.experiments.chi.chiserver.domain.scorer.OfferScore;
import pl.allegro.experiments.chi.chiserver.domain.scorer.OfferScoreRepository;
import pl.allegro.experiments.chi.chiserver.domain.scorer.Score;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class MongoOfferScoreRepository implements OfferScoreRepository {
    private final MongoExperimentOfferRepository experimentOfferRepository;

    public MongoOfferScoreRepository(MongoExperimentOfferRepository experimentOfferRepository) {
        this.experimentOfferRepository = experimentOfferRepository;
    }

    @Override
    public List<OfferScore> scores() {
        ExperimentOffer experimentOffer = experimentOfferRepository.get();

        Map<String, OfferScore> randomScoreDefaults = randomScoreDefaults(experimentOffer.getOffers());
        Map<String, OfferScore> result = applyCalculatedScores(randomScoreDefaults, experimentOffer.getOfferScores());

        return ImmutableList.copyOf(result.values().stream()
                .sorted(Comparator.comparingDouble(it -> -it.getScore().getValue()))
                .collect(Collectors.toList()));
    }

    private Map<String, OfferScore> randomScoreDefaults(List<Offer> offers) {
        Map<String, OfferScore> defaults = new HashMap<>();
        for (OfferScore offerScore: scoreRandomly(offers)) {
            defaults.put(offerScore.getOffer().getOfferId(), offerScore);
        }
        return defaults;
    }

    private List<OfferScore> scoreRandomly(List<Offer> offers) {
        return ImmutableList.copyOf(offers.stream()
                .map(it -> OfferScore.of(
                        it,
                        Score.of(Math.random() * 10)))
                .sorted(Comparator.comparingDouble(it -> -it.getScore().getValue()))
                .collect(Collectors.toList()));
    }

    private Map<String, OfferScore> applyCalculatedScores(
            Map<String, OfferScore> randomDefaults,
            List<OfferScore> offerScores) {
        for (OfferScore offerScore: offerScores) {
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
        ExperimentOffer experimentOffer = experimentOfferRepository.get();

        Map<String, OfferScore>  currentScores = ImmutableList.copyOf(experimentOffer.getOfferScores()).stream()
                .collect(Collectors.toMap(it -> it.getOffer().getOfferId(), it -> it));

        for (OfferScore offerScore: offerScoreUpdate) {
            String offerId = offerScore.getOffer().getOfferId();
            if (currentScores.containsKey(offerId)) {
                currentScores.put(offerId, currentScores.get(offerId).plus(offerScore));
            } else {
                currentScores.put(offerId, offerScore);
            }
        }
        experimentOfferRepository.save(
                experimentOffer.withOfferScores(
                        ImmutableList.copyOf(currentScores.values())));
    }
}