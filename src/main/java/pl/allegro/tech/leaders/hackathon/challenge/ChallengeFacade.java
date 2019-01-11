package pl.allegro.tech.leaders.hackathon.challenge;

import pl.allegro.tech.leaders.hackathon.challenge.api.*;
import pl.allegro.tech.leaders.hackathon.registration.RegistrationService;
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

    public Flux<ChallengeResultDto> executeChallenge(String challengeId) {
        return Flux.empty();
    }

    public Mono<ChallengeResultDto> getChallengeResult(String challengeId, String teamId) {
        return Mono.empty();
    }

    public Flux<ChallengeResultDto> getChallengeResult(String challengeId) {
        return Flux.empty();
    }

    public Mono<HackatonResultDto> getHackatonResults() {
        return Mono.empty();
    }

    public List<ChallengeDetailsDto> getActiveChallenges() {
        return challenges.stream()
                .map(Challenge::toChallengeDetailsDto)
                .collect(toList());
    }

    public Optional<ChallengeDetailsDto> getActiveChallenge(String challengeId) {
        return challenges.stream()
                .filter(challenge -> challenge.getId().equals(challengeId))
                .map(Challenge::toChallengeDetailsDto)
                .findFirst();
    }
}
