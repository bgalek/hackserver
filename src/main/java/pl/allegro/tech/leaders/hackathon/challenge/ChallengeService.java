package pl.allegro.tech.leaders.hackathon.challenge;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

class ChallengeService {

    private static final Logger logger = LoggerFactory.getLogger(ChallengeService.class);

    ChallengeService(List<Challenge> challenges) {
        challenges.forEach(challenge -> logger.info("initialized challenge: {}, {}", challenge.getName(), challenge.getDescription()));
    }
}
