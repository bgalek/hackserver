package pl.allegro.tech.leaders.hackathon.challenge;

import org.springframework.data.repository.Repository;
import pl.allegro.tech.leaders.hackathon.challenge.Challenge.ChallengeState;
import pl.allegro.tech.leaders.hackathon.challenge.api.ChallengeNotFoundException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

class ChallengeRepository {
    private final ChallengeStateRepository challengeStateRepository;
    private final ChallengeCreator challengeCreator;

    ChallengeRepository(
            ChallengeStateRepository challengeStateRepository, ChallengeCreator challengeCreator) {
        this.challengeStateRepository = challengeStateRepository;
        this.challengeCreator = challengeCreator;
    }

    Mono<Challenge> save(Challenge challenge) {
        return challengeStateRepository.save(challenge.toState())
                .map(challengeCreator::restoreChallenge);
    }

    Mono<Challenge> findById(String id) {
        return challengeStateRepository.findById(id)
                .map(challengeCreator::restoreChallenge);
    }

    Mono<Challenge> findByIdOrThrow(String id) {
        return findById(id)
                .switchIfEmpty(Mono.error(new ChallengeNotFoundException(id)));
    }

    Flux<Challenge> findAll() {
        return challengeStateRepository.findAll()
                .map(challengeCreator::restoreChallenge);
    }
}

interface ChallengeStateRepository extends Repository<ChallengeState, String> {
    Mono<ChallengeState> save(ChallengeState challengeState);

    Mono<ChallengeState> findById(String id);

    Flux<ChallengeState> findAll();
}
