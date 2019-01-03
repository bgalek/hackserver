package pl.allegro.tech.leaders.hackathon.challenge;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/challenges")
class ChallengeController {

    private final ChallengeService challengeService;

    ChallengeController(ChallengeService challengeService) {
        this.challengeService = challengeService;
    }

    @GetMapping
    List<ChallengeResponse> listChallanges() {
        return challengeService.getAll().stream()
                .map(ChallengeResponse::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    ChallengeDetailsResponse getChallange(@PathVariable String id) {
        return challengeService.get(id)
                .map(ChallengeDetailsResponse::new)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Challenge Not Found"));
    }

}
