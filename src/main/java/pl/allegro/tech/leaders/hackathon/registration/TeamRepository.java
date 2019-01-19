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

    Flux<Team> findAll() {
        return persistenceTeamRepository.findAll();
    }

    Mono<Team> findByName(String name) {
        return persistenceTeamRepository.findByName(name);
    }
}

interface PersistenceTeamRepository extends Repository<Team, String> {
    Mono<Team> save(Team team);

    Mono<Team> findByName(String name);

    Flux<Team> findAll();
}
