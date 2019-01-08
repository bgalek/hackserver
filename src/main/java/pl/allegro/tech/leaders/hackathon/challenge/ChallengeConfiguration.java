package pl.allegro.tech.leaders.hackathon.challenge;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.allegro.tech.leaders.hackathon.challenge.api.Challenge;

import java.util.List;

@Configuration
class ChallengeConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(ChallengeConfiguration.class);

    @Bean
    ChallengeFacade challengeService(List<Challenge> challenges) {
        challenges.forEach(challenge -> logger.info("Initialized challenge: {}, {}, {}", challenge.getId(), challenge.getName(), challenge.getDescription()));
        return new ChallengeFacade(challenges);
    }
}
