package pl.allegro.experiments.chi.chiserver.domain.scorer;

import java.util.List;

public interface OfferScoreRepository {
    List<OfferScore> scores();

    void setScores(List<OfferScore> offerScores);
}
