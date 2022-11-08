package pl.allegro.tech.leaders.hackathon.registration;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.repository.Repository;
import pl.allegro.tech.leaders.hackathon.registration.api.HealthStatus;
import pl.allegro.tech.leaders.hackathon.registration.api.TeamSecret;
import pl.allegro.tech.leaders.hackathon.registration.api.TeamUpdatedEvent;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

class TeamRepository {

    private final PersistenceTeamRepository persistenceTeamRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    TeamRepository(PersistenceTeamRepository persistenceTeamRepository,
                   ApplicationEventPublisher applicationEventPublisher) {
        this.persistenceTeamRepository = persistenceTeamRepository;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    Mono<Team> save(Team team) {
        return persistenceTeamRepository.save(team)
                .doOnSuccess(it -> applicationEventPublisher
                        .publishEvent(new TeamUpdatedEvent(it.getName(), HealthStatus.UNKNOWN)));
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
