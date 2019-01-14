package pl.allegro.tech.leaders.hackathon.challenge;

import pl.allegro.tech.leaders.hackathon.challenge.api.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

public class ChallengeFacade {
    private final List<Challenge> challenges;

    ChallengeFacade(List<Challenge> challenges) {
        this.challenges = requireNonNull(challenges);
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

    public List<ChallengeDetails> getActiveChallenges() {
        return challenges.stream()
                .map(Challenge::toChallengeDetailsDto)
                .collect(toList());
    }

    public Optional<ChallengeDetails> getActiveChallenge(String challengeId) {
        return challenges.stream()
                .filter(challenge -> challenge.getId().equals(challengeId))
                .map(Challenge::toChallengeDetailsDto)
                .findFirst();
    }
}
