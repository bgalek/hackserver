package pl.allegro.tech.leaders.hackathon.challenge;

import pl.allegro.tech.leaders.hackathon.challenge.api.ChallengeNotFoundException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static java.util.Comparator.comparingLong;

class ChallengeProvider {
    private final ChallengeRepository challengeRepository;

    ChallengeProvider(ChallengeRepository challengeRepository) {
        this.challengeRepository = challengeRepository;
    }

    Flux<Challenge> getActiveChallenges() {
        return challengeRepository.findAll()
                .filter(Challenge::isActive)
                .sort(comparingLong(c -> -1 * c.getActivatedAt().toEpochMilli()));
    }

    Mono<Challenge> getActiveChallenge(String challengeId) {
        return challengeRepository.findById(challengeId)
                .filter(Challenge::isActive)
                .switchIfEmpty(Mono.error(new ChallengeNotFoundException(challengeId)));
    }
}
