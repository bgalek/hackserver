package pl.allegro.tech.leaders.hackathon.challenge.infrastucture;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import pl.allegro.tech.leaders.hackathon.challenge.ChallengeFacade;
import pl.allegro.tech.leaders.hackathon.challenge.api.ChallengeDetailsDto;

import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequestMapping("/challenges")
class ChallengeController {
    private final ChallengeFacade challengeFacade;

    ChallengeController(ChallengeFacade challengeFacade) {
        this.challengeFacade = challengeFacade;
    }

    @GetMapping
    List<ChallengeDetailsDto> listActiveChallanges() {
        return challengeFacade.getActiveChallenges();
    }

    @GetMapping("/{id}")
    ChallengeDetailsDto getChallange(@PathVariable String id) {
        return challengeFacade.getActiveChallenge(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Challenge Not Found"));
    }
}
