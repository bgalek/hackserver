package pl.allegro.tech.leaders.hackathon.challenge;

import pl.allegro.tech.leaders.hackathon.registration.RegistrationFacade;
import pl.allegro.tech.leaders.hackathon.registration.api.RegisteredTeam;
import reactor.core.publisher.Mono;

import static java.util.function.Function.identity;

class ChallengeExampleRunner {
    private final ChallengeProvider challengeProvider;
    private final RegistrationFacade registrationFacade;
    private final TaskRunner taskRunner;

    ChallengeExampleRunner(
            ChallengeProvider challengeProvider,
            RegistrationFacade registrationFacade,
            TaskRunner taskRunner) {
        this.challengeProvider = challengeProvider;
        this.registrationFacade = registrationFacade;
        this.taskRunner = taskRunner;
    }

    Mono<ChallengeResult> runChallengeExample(String challengeId, String teamId) {
        return getChallengeDefinition(challengeId)
                .zipWith(getTeam(teamId), this::runChallengeExample)
                .flatMap(identity());
    }

    private Mono<ChallengeResult> runChallengeExample(ChallengeDefinition challenge, RegisteredTeam team) {
        return taskRunner.run(challenge, challenge.getExample(), team);
    }

    private Mono<ChallengeDefinition> getChallengeDefinition(String challengeId) {
        return challengeProvider.getActiveChallenge(challengeId)
                .map(Challenge::getDefinition);
    }

    private Mono<RegisteredTeam> getTeam(String teamId) {
        return registrationFacade.getTeamByName(teamId);
    }
}
