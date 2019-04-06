package pl.allegro.tech.leaders.hackathon.registration

import org.springframework.http.ResponseEntity
import pl.allegro.tech.leaders.hackathon.registration.api.HealthCheckClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import spock.lang.Specification

import static org.springframework.http.HttpStatus.OK

class RegistrationSpec extends Specification {

    TeamRepository teamRepository = new TeamRepository(new InMemoryTeamRepository())
    RegistrationFacade registrationFacade = new RegistrationFacade(teamRepository, new HealthCheckMonitor(new DefaultHealthCheckClient()))

    Team createTeam(String ip, int port) {
        new Team('team name', new InetSocketAddress(InetAddress.getByName(ip), port))
    }

    Mono<Team> registerTeam(Team team) {
        teamRepository.save(team)
    }

    class InMemoryTeamRepository implements PersistenceTeamRepository {
        private final Map<String, Team> store = new HashMap<>()

        @Override
        Mono<Team> save(Team team) {
            if (team.getId() == null) team.id = UUID.randomUUID().toString()
            store.put(team.getId(), team)
            return Mono.just(team)
        }

        @Override
        Mono<Team> findByName(String name) {
            Team team = store.values().find { it.name == name }
            return team != null ? Mono.just(team) : Mono.<Team> empty()
        }

        @Override
        Mono<Team> findById(String id) {
            return Mono.just(store.get(id))
        }

        @Override
        Mono<Team> findByNameAndSecret(String name, String secret) {
            return Optional.ofNullable(store.values().find({
                it.name == name && it.secret == secret
            }))
                    .map({ Mono.just(it) })
                    .orElse(Mono.empty())
        }

        @Override
        Flux<Team> findAll() {
            return Flux.fromIterable(store.values())
        }
    }

    class DefaultHealthCheckClient implements HealthCheckClient {
        @Override
        Mono<ResponseEntity<Void>> execute(URI uri) {
            Mono.just(new ResponseEntity(null, OK))
        }
    }
}
