package pl.allegro.tech.leaders.hackathon.scores

import org.springframework.context.ApplicationEvent
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.domain.Sort
import pl.allegro.tech.leaders.hackathon.challenge.base.UpdatableFixedClock
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import spock.lang.Specification

import static org.springframework.data.domain.Sort.Direction.DESC
import static pl.allegro.tech.leaders.hackathon.challenge.base.UpdatableFixedClock.defaultClock

abstract class ScoresSpec extends Specification {
    UpdatableFixedClock clock = defaultClock()

    ScoresFacade facade = new ScoresConfiguration()
            .scoresFacade(clock, new InMemoryScoresRepository(), new InMemoryApplicationEventPublisher())
}

class InMemoryApplicationEventPublisher implements ApplicationEventPublisher {
    List<Object> events = []

    @Override
    void publishEvent(ApplicationEvent event) {
        events.add(event)
    }

    @Override
    void publishEvent(Object event) {
        events.add(event)
    }
}

class InMemoryScoresRepository implements PersistenceScoresRepository {
    private final Map<String, Scores> store = new HashMap<>()

    @Override
    Mono<Scores> save(Scores scores) {
        store.put(scores.id, scores)
        return Mono.just(scores)
    }

    @Override
    Flux<Scores> findAll(Sort sort) {
        if (sort != Sort.by(DESC, "version")) {
            throw new IllegalArgumentException("Supports only descending version sort")
        }
        return Flux.fromIterable(sortedScoresByVersion())
    }

    private List<Scores> sortedScoresByVersion() {
        return store.values()
                .sort { it.version }
                .reverse()
    }
}
