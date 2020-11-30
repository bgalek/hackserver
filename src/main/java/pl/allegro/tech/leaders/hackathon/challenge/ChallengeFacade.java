package pl.allegro.tech.leaders.hackathon.challenge;

import pl.allegro.tech.leaders.hackathon.challenge.api.ChallengeActivationResult;
import pl.allegro.tech.leaders.hackathon.challenge.api.ChallengeDetails;
import pl.allegro.tech.leaders.hackathon.challenge.api.TaskResult;
import pl.allegro.tech.leaders.hackathon.registration.api.TeamSecret;
import pl.allegro.tech.leaders.hackathon.scores.api.ScoreRegistry;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;

public class ChallengeFacade {
    private final ChallengeActivator challengeActivator;
    private final ChallengeRegistrar challengeRegistrar;
    private final ChallengeProvider challengeProvider;
    private final ChallengeRunner challengeRunner;
    private final ChallengeExampleRunner challengeExampleRunner;
    private final ChallengeResultRepository challengeResultRepository;
    private final ScoreRegistry scoreRegistry;

    ChallengeFacade(
            ChallengeActivator challengeActivator,
            ChallengeRegistrar challengeRegistrar,
            ChallengeProvider challengeProvider,
            ChallengeRunner challengeRunner,
            ChallengeExampleRunner challengeExampleRunner,
            ChallengeResultRepository challengeResultRepository,
            ScoreRegistry scoreRegistry) {
        this.challengeActivator = challengeActivator;
        this.challengeRegistrar = challengeRegistrar;
        this.challengeProvider = challengeProvider;
        this.challengeRunner = challengeRunner;
        this.challengeExampleRunner = challengeExampleRunner;
        this.challengeResultRepository = challengeResultRepository;
        this.scoreRegistry = scoreRegistry;
    }

    public Flux<ChallengeDetails> registerChallengeDefinitions(
            List<ChallengeDefinition> challengeDefinitions) {
        return challengeRegistrar.registerChallengeDefinitions(challengeDefinitions);
    }

    public Mono<ChallengeActivationResult> activateChallenge(String challengeId) {
        Objects.requireNonNull(challengeId);
        return challengeActivator.activateChallenge(challengeId);
    }

    public Mono<ChallengeActivationResult> deactivateChallenge(String challengeId) {
        Objects.requireNonNull(challengeId);
        return challengeActivator.deactivateChallenge(challengeId);
    }

    public Flux<ChallengeDetails> getActiveChallenges() {
        return challengeProvider.getActiveChallenges()
                .map(Challenge::toChallengeDetailsDto);
    }

    Mono<Challenge> getActiveChallenge(String challengeId) {
        return challengeProvider.getActiveChallenge(challengeId);
    }

    public Mono<ChallengeDetails> getActiveChallengeDetails(String challengeId) {
        return getActiveChallenge(challengeId).map(Challenge::toChallengeDetailsDto);
    }

    public Mono<TaskResult> runExampleTask(String challengeId, String teamId) {
        return challengeExampleRunner.runChallengeExample(challengeId, teamId)
                .map(ChallengeResult::toTaskResult);
    }

    public Flux<TaskResult> runChallenge(String challengeId) {
        return challengeRunner.runChallenge(challengeId)
                .map(ChallengeResult::toTaskResult)
                .transformDeferred(scoreRegistry::updateScores);
    }

    public Flux<TaskResult> runChallenge(String challengeId, String teamId) {
        return challengeRunner.runChallenge(challengeId, teamId)
                .map(ChallengeResult::toTaskResult)
                .transformDeferred(scoreRegistry::updateScores);
    }

    public Flux<TaskResult> runChallenge(String challengeId, String teamId, TeamSecret secret) {
        return challengeRunner.runChallenge(challengeId, teamId, secret)
                .map(ChallengeResult::toTaskResult)
                .transformDeferred(scoreRegistry::updateScores);
    }

    public Flux<TaskResult> getResultsForChallenge(String challengeId) {
        return challengeResultRepository.findByChallengeId(challengeId)
                .map(ChallengeResult::toTaskResult);
    }

    public Flux<TaskResult> getResultsForTeam(String teamId) {
        return challengeResultRepository.findByTeamId(teamId)
                .map(ChallengeResult::toTaskResult);
    }
}
