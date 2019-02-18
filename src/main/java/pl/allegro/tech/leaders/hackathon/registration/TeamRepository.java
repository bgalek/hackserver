package pl.allegro.tech.leaders.hackathon.registration;

import org.springframework.data.repository.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

class TeamRepository {

    private final PersistenceTeamRepository persistenceTeamRepository;

    TeamRepository(PersistenceTeamRepository persistenceTeamRepository) {
        this.persistenceTeamRepository = persistenceTeamRepository;
    }

    Mono<Team> save(Team team) {
        return persistenceTeamRepository.save(team);
    }

    Mono<Void> delete(String name, String secret) {
        return persistenceTeamRepository.findByNameAndSecret(name, secret)
                .switchIfEmpty(Mono.error(new BadSecretException(name)))
                .flatMap(persistenceTeamRepository::delete);
    }

    Flux<Team> findAll() {
        return persistenceTeamRepository.findAll();
    }

    Mono<Team> findByName(String name) {
        return persistenceTeamRepository.findByName(name)
                .switchIfEmpty(Mono.error(new TeamNotFoundException(name)));
    }
}

interface PersistenceTeamRepository extends Repository<Team, String> {
    Mono<Team> save(Team team);

    Mono<Team> findByName(String name);

    Mono<Team> findByNameAndSecret(String name, String secret);

    Mono<Void> delete(Team team);

    Flux<Team> findAll();
}
