package pl.allegro.experiments.chi.chiserver.infrastructure.scorer;

import com.google.common.collect.ImmutableList;
import org.springframework.stereotype.Repository;
import pl.allegro.experiments.chi.chiserver.domain.scorer.*;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class RandomOfferScoreRepository implements OfferScoreRepository {
    private final OfferRepository offerRepository;

    public RandomOfferScoreRepository(OfferRepository offerRepository) {
        this.offerRepository = offerRepository;
    }

    @Override
    public List<OfferScore> scores() {
        List<Offer> offers = offerRepository.all();
        List<OfferScore> scores = offers.stream()
                .map(it -> OfferScore.of(
                        it,
                        Score.of(Math.random())))
                .collect(Collectors.toList());
        double randomSum = scores.stream().mapToDouble(it -> it.getScore().getValue()).sum();
        var result = scores.stream()
                .map(it -> OfferScore.of(
                        it.getOffer(),
                        Score.of(it.getScore().getValue() / randomSum)))
                .sorted(Comparator.comparingDouble(it -> it.getScore().getValue()))
                .collect(Collectors.toList());
        return ImmutableList.copyOf(result);
    }
}
