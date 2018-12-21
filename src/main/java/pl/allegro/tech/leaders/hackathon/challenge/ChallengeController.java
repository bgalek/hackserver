package pl.allegro.tech.leaders.hackathon.challenge;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/challenges")
class ChallengeController {

    private final List<Challenge> challenges;

    ChallengeController(List<Challenge> challenges) {
        this.challenges = challenges;
    }

    @GetMapping
    List<Challenge> getChallanges() {
        return challenges;
    }

}
