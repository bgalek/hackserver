package pl.allegro.tech.leaders.hackathon.challenge;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
class ChallengesConfig {

    private static final Logger logger = LoggerFactory.getLogger(ChallengesConfig.class);

    @Bean
    ChallengeService challengeService(List<Challenge> challenges) {
        challenges.forEach(challenge -> logger.info("initialized challenge: {}, {}", challenge.getName(), challenge.getDescription()));
        return new ChallengeService(challenges);
    }
}
