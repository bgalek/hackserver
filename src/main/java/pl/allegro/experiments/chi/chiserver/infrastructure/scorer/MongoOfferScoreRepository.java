package pl.allegro.experiments.chi.chiserver.infrastructure.scorer;

import org.springframework.stereotype.Repository;
import pl.allegro.experiments.chi.chiserver.domain.scorer.OfferScore;
import pl.allegro.experiments.chi.chiserver.domain.scorer.OfferScoreRepository;

import java.util.*;

@Repository
public class MongoOfferScoreRepository implements OfferScoreRepository {
    private final MongoExperimentOfferRepository experimentOfferRepository;

    public MongoOfferScoreRepository(MongoExperimentOfferRepository experimentOfferRepository) {
        this.experimentOfferRepository = experimentOfferRepository;
    }

    @Override
    public List<OfferScore> scores() {
        return experimentOfferRepository.get().randomizedScores();
    }

    @Override
    public void updateScores(List<OfferScore> offerScoreUpdate) {
        ExperimentOffer experimentOffer = experimentOfferRepository.get();
        experimentOfferRepository.save(experimentOffer.withUpdatedOfferScores(offerScoreUpdate));
    }
}