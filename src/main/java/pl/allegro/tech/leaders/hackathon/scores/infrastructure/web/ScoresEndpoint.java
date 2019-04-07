package pl.allegro.tech.leaders.hackathon.scores.infrastructure.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.allegro.tech.leaders.hackathon.scores.ScoresFacade;
import pl.allegro.tech.leaders.hackathon.scores.api.ChallengeScores;
import pl.allegro.tech.leaders.hackathon.scores.api.HackatonScores;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/scores")
public class ScoresEndpoint {
    private final ScoresFacade scoresFacade;

    ScoresEndpoint(ScoresFacade scoresFacade) {
        this.scoresFacade = scoresFacade;
    }

    @GetMapping
    Mono<HackatonScores> getHackatonScores() {
        return scoresFacade.getHackatonScores();
    }

    @GetMapping("/{challengeId}")
    Mono<ChallengeScores> getChallengeScores(@PathVariable("challengeId") String challengeId) {
        return scoresFacade.getChallengeScores(challengeId);
    }
}
