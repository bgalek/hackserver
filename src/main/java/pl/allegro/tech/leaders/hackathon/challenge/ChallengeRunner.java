package pl.allegro.tech.leaders.hackathon.challenge;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.allegro.tech.leaders.hackathon.registration.RegistrationFacade;
import pl.allegro.tech.leaders.hackathon.registration.api.RegisteredTeam;
import pl.allegro.tech.leaders.hackathon.registration.api.TeamSecret;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

class ChallengeRunner {
    private static final int FLAT_MAP_ONE_AFTER_ANOTHER = 1;
    private final ChallengeProvider challengeProvider;
    private final RegistrationFacade registrationFacade;
    private final TaskRunner taskRunner;
    private final ChallengeResultRepository challengeResultRepository;

    private static final Logger logger = LoggerFactory.getLogger(TaskRunner.class);

    ChallengeRunner(
            ChallengeProvider challengeProvider,
            RegistrationFacade registrationFacade,
            TaskRunner taskRunner, ChallengeResultRepository challengeResultRepository) {
        this.challengeProvider = challengeProvider;
        this.registrationFacade = registrationFacade;
        this.taskRunner = taskRunner;
        this.challengeResultRepository = challengeResultRepository;
    }

    Flux<ChallengeResult> runChallenge(String challengeId) {
        return getChallengeDefinition(challengeId)
                .flatMapMany(this::runChallenge);
    }

    Flux<ChallengeResult> runChallenge(String challengeId, String teamId) {
        return getChallengeDefinition(challengeId)
                .flatMapMany(challenge -> runChallenge(challenge, teamId));
    }

    Flux<ChallengeResult> runChallenge(String challengeId, String teamId, TeamSecret secret) {
        return registrationFacade.getTeamByNameAndSecret(teamId, secret)
                .flatMapMany(registeredTeam -> runChallenge(challengeId, registeredTeam));
    }

    private Flux<ChallengeResult> runChallenge(String challengeId, RegisteredTeam team) {
        return getChallengeDefinition(challengeId)
                .flatMapMany(it -> runChallenge(it, team));
    }

    private Flux<ChallengeResult> runChallenge(ChallengeDefinition challenge, String teamId) {
        return registrationFacade.getTeamById(teamId)
                .flatMapMany(team -> runChallenge(challenge, team));
    }

    private Flux<ChallengeResult> runChallenge(ChallengeDefinition challenge) {
        return registrationFacade.getAll()
                .flatMap(team -> runChallenge(challenge, team));
    }

    private Flux<ChallengeResult> runChallenge(ChallengeDefinition challenge, RegisteredTeam team) {
        return Flux.fromIterable(challenge.getTasks())
                .map(task -> taskRunner.run(challenge, task, team))
                .map(Mono::block)
                .flatMap(result -> {
                    logger.info("Got result: {}, score: {}",
                            result.toTaskResult().getResponseBody(),
                            result.toTaskResult().getScore()
                    );
                    return saveResult(result);
                }, FLAT_MAP_ONE_AFTER_ANOTHER)
                .takeUntil(result -> result.toTaskResult().getScore() == 0);
    }

    private Mono<ChallengeResult> saveResult(ChallengeResult result) {
        return challengeResultRepository.save(result);
    }

    private Mono<ChallengeDefinition> getChallengeDefinition(String challengeId) {
        return challengeProvider.getActiveChallenge(challengeId)
                .map(Challenge::getDefinition);
    }
}
