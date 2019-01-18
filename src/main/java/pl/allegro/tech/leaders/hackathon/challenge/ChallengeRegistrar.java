package pl.allegro.tech.leaders.hackathon.challenge;

import pl.allegro.tech.leaders.hackathon.challenge.api.ChallengeDefinition;
import pl.allegro.tech.leaders.hackathon.challenge.api.ChallengeDetails;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

class ChallengeRegistrar {
    private final ChallengeRepository challengeRepository;
    private final ChallengeCreator challengeCreator;

    ChallengeRegistrar(
            ChallengeRepository challengeRepository,
            ChallengeCreator challengeCreator) {
        this.challengeRepository = challengeRepository;
        this.challengeCreator = challengeCreator;
    }

    Flux<ChallengeDetails> registerChallengeDefinitions(List<ChallengeDefinition> challengeDefinitions) {
        return Flux.fromIterable(challengeDefinitions)
                .map(challengeCreator::createChallenge)
                .flatMap(this::saveIfNotPersisted)
                .map(Challenge::toChallengeDetailsDto);
    }

    private Mono<Challenge> saveIfNotPersisted(Challenge challenge) {
        return challengeRepository.findById(challenge.getId())
                .switchIfEmpty(Mono.defer(() -> challengeRepository.save(challenge)));
    }
}
