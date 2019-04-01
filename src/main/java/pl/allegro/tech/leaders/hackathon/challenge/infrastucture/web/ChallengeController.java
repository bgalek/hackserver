package pl.allegro.tech.leaders.hackathon.challenge.infrastucture.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.allegro.tech.leaders.hackathon.challenge.ChallengeFacade;
import pl.allegro.tech.leaders.hackathon.challenge.api.ChallengeActivationResult;
import pl.allegro.tech.leaders.hackathon.challenge.api.ChallengeDetails;
import pl.allegro.tech.leaders.hackathon.challenge.api.TaskResult;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/challenges")
class ChallengeController {
    private final ChallengeFacade challengeFacade;

    ChallengeController(ChallengeFacade challengeFacade) {
        this.challengeFacade = challengeFacade;
    }

    @GetMapping
    Flux<ChallengeDetails> listActiveChallenges() {
        return challengeFacade.getActiveChallenges();
    }

    @GetMapping("/{id}")
    Mono<ChallengeDetails> getActiveChallenge(@PathVariable("id") String id) {
        return challengeFacade.getActiveChallengeDetails(id);
    }

    @PutMapping("/{id}/activate")
    Mono<ChallengeActivationResult> activate(@PathVariable("id") String id) {
        return challengeFacade.activateChallenge(id);
    }

    @PutMapping("/{id}/deactivate")
    Mono<ChallengeActivationResult> deactivate(@PathVariable("id") String id) {
        return challengeFacade.deactivateChallenge(id);
    }

    @GetMapping("/{id}/run-example")
    Mono<TaskResult> runExampleTask(
            @PathVariable("id") String challengeId,
            @RequestParam("team-id") String teamId) {
        return challengeFacade.runExampleTask(challengeId, teamId);
    }

    @GetMapping("/results")
    Flux<TaskResult> results(@RequestParam("team-id") String teamId) {
        return challengeFacade.getResultsForTeam(teamId);
    }
}
