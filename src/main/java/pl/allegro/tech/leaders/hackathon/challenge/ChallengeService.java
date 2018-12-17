package pl.allegro.tech.leaders.hackathon.challenge;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

class ChallengeService {

    private static final Logger logger = LoggerFactory.getLogger(ChallengeService.class);

    ChallengeService(List<Challenge> challenges) {
        logger.info("initialized {} challenge: {}", challenges.size(), challenges.stream().map(Challenge::getName).collect(Collectors.joining()));
    }
}
