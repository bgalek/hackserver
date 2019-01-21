package pl.allegro.tech.leaders.hackathon.challenge.infrastucture;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.allegro.tech.leaders.hackathon.challenge.ChallengeFacade;
import pl.allegro.tech.leaders.hackathon.challenge.api.ChallengeActivationResult;
import pl.allegro.tech.leaders.hackathon.challenge.api.ChallengeDetails;
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
    Mono<ChallengeDetails> getActiveChallenge(@PathVariable String id) {
        return challengeFacade.getActiveChallengeDetails(id);
    }

    @PutMapping("/{id}/activate")
    Mono<ChallengeActivationResult> activate(@PathVariable String id) {
        return challengeFacade.activateChallenge(id);
    }

    @PutMapping("/{id}/deactivate")
    Mono<ChallengeActivationResult> deactivate(@PathVariable String id) {
        return challengeFacade.deactivateChallenge(id);
    }
}
