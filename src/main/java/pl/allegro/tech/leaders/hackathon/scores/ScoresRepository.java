package pl.allegro.tech.leaders.hackathon.scores;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.Repository;
import pl.allegro.tech.leaders.hackathon.scores.api.ScoreUpdatedEvent;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;

import static org.springframework.data.domain.Sort.Direction.DESC;

class ScoresRepository {

    private final PersistenceScoresRepository persistenceScoresRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    ScoresRepository(PersistenceScoresRepository persistenceScoresRepository,
                     ApplicationEventPublisher applicationEventPublisher) {
        this.persistenceScoresRepository = persistenceScoresRepository;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public Mono<Scores> save(Scores scores) {
        return persistenceScoresRepository.save(scores)
                .doOnSuccess(it -> applicationEventPublisher
                        .publishEvent(new ScoreUpdatedEvent(scores.getHackatonScores())));
    }

    public Flux<Scores> findAll(Sort sort) {
        return persistenceScoresRepository.findAll(sort);
    }

    Mono<Scores> findLast() {
        return this.findAll(Sort.by(DESC, "version"))
                .switchIfEmpty(Mono.just(new Scores(Instant.now())))
                .next();
    }
}

interface PersistenceScoresRepository extends Repository<Scores, String> {
    Mono<Scores> save(Scores scores);

    Flux<Scores> findAll(Sort sort);
}
