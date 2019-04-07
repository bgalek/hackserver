package pl.allegro.tech.leaders.hackathon.scores;

import pl.allegro.tech.leaders.hackathon.challenge.api.TaskResult;
import pl.allegro.tech.leaders.hackathon.scores.api.ChallengeScores;
import pl.allegro.tech.leaders.hackathon.scores.api.HackatonScores;
import pl.allegro.tech.leaders.hackathon.scores.api.ScoreRegistry;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class ScoresFacade implements ScoreRegistry {
    private final ScoresRepository repository;
    private final ScoresUpdater scoresUpdater;

    ScoresFacade(ScoresRepository repository, ScoresUpdater scoresUpdater) {
        this.repository = repository;
        this.scoresUpdater = scoresUpdater;
    }

    @Override
    public Flux<TaskResult> updateScores(Flux<TaskResult> taskResults) {
        return scoresUpdater.updateScores(taskResults);
    }

    public Mono<ChallengeScores> getChallengeScores(String challengeId) {
        return repository.findLast()
                .map(scores -> scores.getChallengeScores(challengeId));
    }

    public Mono<HackatonScores> getHackatonScores() {
        return repository.findLast()
                .map(Scores::getHackatonScores);
    }
}
