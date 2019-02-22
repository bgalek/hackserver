package pl.allegro.tech.leaders.hackathon.registration;

import org.springframework.data.repository.Repository;
import pl.allegro.tech.leaders.hackathon.registration.api.TeamSecret;
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

    Mono<Team> findById(String id) {
        return persistenceTeamRepository.findById(id);
    }

    Mono<Team> find(String name) {
        return persistenceTeamRepository.findByName(name);
    }

    Mono<Team> find(String name, TeamSecret teamSecret) {
        return persistenceTeamRepository.findByNameAndSecret(name, teamSecret.getSecret());
    }
}

interface PersistenceTeamRepository extends Repository<Team, String> {
    Mono<Team> save(Team team);

    Mono<Team> findById(String id);

    Mono<Team> findByName(String name);

    Mono<Team> findByNameAndSecret(String name, String secret);

    Flux<Team> findAll();
}
