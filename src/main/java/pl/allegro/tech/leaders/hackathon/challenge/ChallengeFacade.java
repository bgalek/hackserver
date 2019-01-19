package pl.allegro.tech.leaders.hackathon.challenge;

import pl.allegro.tech.leaders.hackathon.challenge.api.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;

public class ChallengeFacade {
    private final ChallengeActivator challengeActivator;
    private final ChallengeRepository challengeRepository;
    private final ChallengeRegistrar challengeRegistrar;

    ChallengeFacade(
            ChallengeActivator challengeActivator,
            ChallengeRepository challengeRepository,
            ChallengeRegistrar challengeRegistrar) {
        this.challengeActivator = challengeActivator;
        this.challengeRepository = challengeRepository;
        this.challengeRegistrar = challengeRegistrar;
    }

    public Flux<ChallengeDetails> registerChallengeDefinitions(List<ChallengeDefinition> challengeDefinitions) {
        return challengeRegistrar.registerChallengeDefinitions(challengeDefinitions);
    }

    public Mono<ChallengeActivationResult> activateChallenge(String challengeId) {
        Objects.requireNonNull(challengeId);
        return challengeActivator.activateChallenge(challengeId);
    }

    public Flux<ChallengeDetails> getActiveChallenges() {
        return challengeRepository.findActivated()
                .map(Challenge::toChallengeDetailsDto);
    }

    Mono<Challenge> getActiveChallenge(String challengeId) {
        Objects.requireNonNull(challengeId);
        return challengeRepository.findById(challengeId)
                .filter(Challenge::isActive)
                .switchIfEmpty(Mono.error(new ChallengeNotFoundException(challengeId)));
    }

    public Mono<ChallengeDetails> getActiveChallengeDetails(String challengeId) {
        return getActiveChallenge(challengeId).map(it -> it.toChallengeDetailsDto());
    }

    public Mono<ChallengeDefinition> getActiveChallengeDefinition(String challengeId) {
        return getActiveChallenge(challengeId).map(it -> it.getDefinition());
    }

    public Flux<ChallengeResult> executeChallenge(String challengeId) {
        return Flux.empty();
    }

    public Mono<ChallengeResult> getChallengeResult(String challengeId, String teamId) {
        return Mono.empty();
    }

    public Flux<ChallengeResult> getChallengeResult(String challengeId) {
        return Flux.empty();
    }

    public Mono<HackatonResult> getHackatonResults() {
        return Mono.empty();
    }

    public void deactivateAll() {
        challengeRepository.deactivateAll();
    }
}
