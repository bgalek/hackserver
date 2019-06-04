package pl.allegro.tech.leaders.hackathon.scores;

import pl.allegro.tech.leaders.hackathon.challenge.api.TaskResult;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Clock;

class ScoresUpdater {
    private final Clock clock;
    private final ScoresRepository repository;

    ScoresUpdater(Clock clock, ScoresRepository repository) {
        this.clock = clock;
        this.repository = repository;
    }

    Flux<TaskResult> updateScores(Flux<TaskResult> taskResults) {
        Scores scores = new Scores(clock.instant());
        return taskResults
                .doOnNext(scores::updateScores)
                .concatWith(this.mergeWithPreviousScoresAndSave(scores));
    }

    private Mono<TaskResult> mergeWithPreviousScoresAndSave(Scores scores) {
        return repository.findLast()
                .flatMap(previousScores -> {
                    scores.mergeWithPrevious(previousScores);
                    return repository.save(scores);
                })
                .flatMap(it -> Mono.empty());
    }
}
