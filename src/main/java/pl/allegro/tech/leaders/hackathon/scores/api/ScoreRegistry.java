package pl.allegro.tech.leaders.hackathon.scores.api;

import pl.allegro.tech.leaders.hackathon.challenge.api.TaskResult;
import reactor.core.publisher.Flux;

public interface ScoreRegistry {
    Flux<TaskResult> updateScores(Flux<TaskResult> results);
}
