package pl.allegro.experiments.chi.chiserver.infrastructure.scorer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.allegro.experiments.chi.chiserver.domain.scorer.OfferRepository;
import pl.allegro.experiments.chi.chiserver.domain.scorer.OfferScoreRepository;

@Configuration
public class OfferScoreRepositoryConfiguration {

    @Bean
    OfferScoreRepository offerScoreRepository(
            OfferRepository offerRepository,
            OfferScoreCrudRepository offerScoreCrudRepository) {
        return new MongoOfferScoreRepository(new RandomOfferScoreRepository(offerRepository), offerScoreCrudRepository);
    }
}
