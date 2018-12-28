package pl.allegro.experiments.chi.chiserver.infrastructure.scorer;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import pl.allegro.experiments.chi.chiserver.domain.scorer.Offer;
import pl.allegro.experiments.chi.chiserver.domain.scorer.OfferScore;
import pl.allegro.experiments.chi.chiserver.domain.scorer.Score;

import java.util.*;
import java.util.stream.Collectors;

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
        return new ExperimentOffer(id, newOffers, Collections.emptyList());
    }

    ExperimentOffer withUpdatedOfferScores(List<OfferScore> offerScoreUpdate) {
        Map<String, OfferScore>  currentScores = ImmutableList.copyOf(this.getOfferScores()).stream()
                .collect(Collectors.toMap(it -> it.getOffer().getOfferId(), it -> it));

        for (OfferScore offerScore: offerScoreUpdate) {
            String offerId = offerScore.getOffer().getOfferId();
            if (currentScores.containsKey(offerId)) {
                currentScores.put(offerId, currentScores.get(offerId).plus(offerScore));
            } else {
                currentScores.put(offerId, offerScore);
            }
        }

        return this.withOfferScores(ImmutableList.copyOf(currentScores.values()));
    }

    private ExperimentOffer withOfferScores(List<OfferScore> newOfferScores) {
        return new ExperimentOffer(id, offers, newOfferScores);
    }

    List<OfferScore> randomizedScores() {
        Map<String, OfferScore> randomScoreDefaults = randomScoreDefaults(this.getOffers());
        Map<String, OfferScore> result = applyCalculatedScores(randomScoreDefaults, this.getOfferScores());

        return ImmutableList.copyOf(result.values().stream()
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
}
