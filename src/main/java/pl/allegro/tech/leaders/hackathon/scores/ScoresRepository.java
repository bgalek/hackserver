package pl.allegro.tech.leaders.hackathon.scores;

import org.springframework.data.domain.Sort;
import org.springframework.data.repository.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.springframework.data.domain.Sort.Direction.DESC;

interface ScoresRepository extends Repository<Scores, String> {
    Mono<Scores> save(Scores scores);

    Flux<Scores> findAll(Sort sort);

    default Mono<Scores> findLast() {
        return this.findAll(Sort.by(DESC, "version")).next();
    }
}
