package pl.allegro.tech.leaders.hackathon.challenge;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
class ChallengeConfiguration {
    @Bean
    ChallengeFacade challengeFacade(Clock clock, ChallengeStateRepository challengeStateRepository) {
        ChallengeCreator creator = new ChallengeCreator();
        ChallengeRepository repository = new ChallengeRepository(challengeStateRepository, creator);
        ChallengeActivator activator = new ChallengeActivator(clock, repository);
        ChallengeRegistrar registrar = new ChallengeRegistrar(repository, creator);
        return new ChallengeFacade(activator, repository, registrar);
    }
}
