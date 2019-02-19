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

    def "should be able to register team"() {
        given: 'registration facade with empty repository'
            TeamRepository teamRepository = new TeamRepository(new InMemoryTeamRepository())
            RegistrationFacade registrationFacade = new RegistrationFacade(teamRepository)
        and: 'a new team registration'
            TeamRegistration teamRegistration = new TeamRegistration('teamName', InetAddress.getLocalHost())
        when: 'team is registered'
            registrationFacade.register(teamRegistration)
        then: 'team is persisted'
            List<Team> persistedTeams = teamRepository.findAll().collectList().block()
            persistedTeams.size() == 1
            persistedTeams.first().getName() == teamRegistration.name
            persistedTeams.first().getRemoteAddress() == teamRegistration.getRemoteAddress()
    }

    def "should be able to update team using secret"() {
        given: 'registration facade with empty repository'
            TeamRepository teamRepository = new TeamRepository(new InMemoryTeamRepository())
            RegistrationFacade registrationFacade = new RegistrationFacade(teamRepository)
        and: 'a new team registration'
            TeamRegistration teamRegistration = new TeamRegistration('teamName', InetAddress.getLocalHost())
        when: 'team is registered'
            RegisteredTeam registeredTeam = registrationFacade.register(teamRegistration).block()
        and: 'team update is created with new IP'
            TeamUpdate teamUpdate = new TeamUpdate(registeredTeam.name,
                    InetAddress.getByName("192.168.1.1"), new ValidTeamSecret(registeredTeam.secret))
        and: 'team is updated'
            registrationFacade.update(teamUpdate).block()
        then:
            List<Team> persistedTeams = teamRepository.findAll().collectList().block()
            persistedTeams.size() == 1
            persistedTeams.stream().findFirst().get().with {
                getName() == teamRegistration.name
                getRemoteAddress() == teamUpdate.remoteAddress
            }
    }

    def "should not be able to update team without secret"() {
        given: 'registration facade with empty repository'
            TeamRepository teamRepository = new TeamRepository(new InMemoryTeamRepository())
            RegistrationFacade registrationFacade = new RegistrationFacade(teamRepository)
        and: 'a new team registration'
            TeamRegistration teamRegistration = new TeamRegistration('teamName', InetAddress.getLocalHost())
        when: 'team is registered'
            RegisteredTeam registeredTeam = registrationFacade.register(teamRegistration).block()
        and: 'team update is created with new IP'
            TeamUpdate teamUpdate = new TeamUpdate(registeredTeam.name, InetAddress.getByName("192.168.1.1"), secret)
        and: 'team is updated'
            registrationFacade.update(teamUpdate).block()
        then:
            thrown(TeamNotFoundException)
        where:
            secret << [new ValidTeamSecret('NOT MY SECRET'), TeamSecret.empty()]
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
