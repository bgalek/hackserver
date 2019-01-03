package pl.allegro.tech.leaders.hackathon.challenge;

import java.util.List;
import java.util.Optional;

class ChallengeService {

    private final List<Challenge> challenges;

    ChallengeService(List<Challenge> challenges) {
        this.challenges = challenges;
    }

    Optional<Challenge> get(String id) {
        return challenges.stream()
                .filter(challenge -> challenge.getId().equals(id))
                .findAny();
    }

    List<Challenge> getAll() {
        return challenges;
    }
}
