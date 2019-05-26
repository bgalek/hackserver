package pl.allegro.tech.leaders.hackathon.scores;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
class ScoresConfiguration {
    @Bean
    ScoresFacade scoresFacade(
            Clock clock,
            PersistenceScoresRepository persistenceScoresRepository, ApplicationEventPublisher applicationEventPublisher) {
        ScoresRepository scoresRepository = new ScoresRepository(persistenceScoresRepository, applicationEventPublisher);
        ScoresUpdater scoresUpdater = new ScoresUpdater(clock, scoresRepository);
        return new ScoresFacade(scoresRepository, scoresUpdater);
    }
}
