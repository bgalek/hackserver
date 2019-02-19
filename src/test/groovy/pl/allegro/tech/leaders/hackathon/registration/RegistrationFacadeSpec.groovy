package pl.allegro.tech.leaders.hackathon.registration

import pl.allegro.tech.leaders.hackathon.registration.api.RegisteredTeam
import pl.allegro.tech.leaders.hackathon.registration.api.TeamRegistration
import pl.allegro.tech.leaders.hackathon.registration.api.TeamSecret
import pl.allegro.tech.leaders.hackathon.registration.api.TeamUpdate
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import spock.lang.Specification

import static pl.allegro.tech.leaders.hackathon.registration.api.TeamSecret.ValidTeamSecret

class RegistrationFacadeSpec extends Specification {

    TeamRepository teamRepository = new TeamRepository(new InMemoryTeamRepository())
    RegistrationFacade registrationFacade = new RegistrationFacade(teamRepository)

    def "should be able to register team"() {
        given: 'team information'
            String teamName = 'teamName'
            InetAddress remoteAddress = InetAddress.getLocalHost()
        when: 'team is registered'
            registerTeam(teamName, remoteAddress)
        then: 'team is persisted'
            List<Team> persistedTeams = teamRepository.findAll().collectList().block()
            persistedTeams.size() == 1
            persistedTeams.first().getName() == teamName
            persistedTeams.first().getRemoteAddress() == remoteAddress
    }

    def "should be able to update team using secret"() {
        when: 'team is registered'
            RegisteredTeam registeredTeam = registerTeam('teamName')
        and: 'team update is created with new IP'
            TeamUpdate teamUpdate = new TeamUpdate(registeredTeam.name, InetAddress.getByName("192.168.1.1"),
                    new ValidTeamSecret(registeredTeam.secret))
        and: 'team is updated'
            registrationFacade.update(teamUpdate).block()
        then:
            List<Team> persistedTeams = teamRepository.findAll().collectList().block()
            persistedTeams.size() == 1
            persistedTeams.first().getName() == registeredTeam.name
            persistedTeams.first().getRemoteAddress() == teamUpdate.remoteAddress
    }

    def "should not be able to update team without secret"() {
        when: 'team is registered'
            RegisteredTeam registeredTeam = registerTeam('teamName')
        and: 'team update is created with new IP'
            TeamUpdate teamUpdate = new TeamUpdate(registeredTeam.name, InetAddress.getByName("192.168.1.1"), secret)
        and: 'team is updated'
            registrationFacade.update(teamUpdate).block()
        then:
            thrown(TeamNotFoundException)
        where:
            secret << [new ValidTeamSecret('NOT MY SECRET'), TeamSecret.empty()]
    }

    private RegisteredTeam registerTeam(String teamName, InetAddress inetAddress = InetAddress.getLocalHost()) {
        TeamRegistration teamRegistration = new TeamRegistration(teamName, inetAddress)
        registrationFacade.register(teamRegistration).block()
    }

    class InMemoryTeamRepository implements PersistenceTeamRepository {
        private final Map<String, Team> store = new HashMap<>()

        @Override
        Mono<Team> save(Team team) {
            store.put(team.getName(), team)
            return Mono.just(team)
        }

        @Override
        Mono<Team> findByName(String name) {
            return Mono.just(store.get(name))
        }

        @Override
        Mono<Team> findByNameAndSecret(String name, String secret) {
            return Optional.ofNullable(store.values().find({ it.name == name && it.secret == secret }))
                    .map({ Mono.just(it) })
                    .orElse(Mono.empty())
        }

        @Override
        Flux<Team> findAll() {
            return Flux.fromIterable(store.values())
        }
    }
}
