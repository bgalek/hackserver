package pl.allegro.tech.leaders.hackathon.challenge;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
class ChallengesConfig {

    @Bean
    ChallengeService challengeService(List<Challenge> challenges) {
        return new ChallengeService(challenges);
    }
}
