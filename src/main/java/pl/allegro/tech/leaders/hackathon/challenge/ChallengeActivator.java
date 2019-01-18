package pl.allegro.tech.leaders.hackathon.challenge;

import pl.allegro.tech.leaders.hackathon.challenge.api.ChallengeActivationResult;
import reactor.core.publisher.Mono;

import java.time.Clock;

class ChallengeActivator {
    private final Clock clock;
    private final ChallengeRepository challengeRepository;

    ChallengeActivator(Clock clock, ChallengeRepository challengeRepository) {
        this.clock = clock;
        this.challengeRepository = challengeRepository;
    }

    Mono<ChallengeActivationResult> activateChallenge(String challengeId) {
        return challengeRepository.findByIdOrThrow(challengeId)
                .doOnNext(challenge -> challenge.activate(clock))
                .flatMap(challengeRepository::save)
                .thenReturn(ChallengeActivationResult.active(challengeId));
    }
}
