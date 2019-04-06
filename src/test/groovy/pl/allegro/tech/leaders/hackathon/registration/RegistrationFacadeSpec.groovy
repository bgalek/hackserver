package pl.allegro.tech.leaders.hackathon.registration

import pl.allegro.tech.leaders.hackathon.registration.api.*

import static pl.allegro.tech.leaders.hackathon.registration.api.TeamSecret.ValidTeamSecret

class RegistrationFacadeSpec extends RegistrationSpec {

    def 'should be able to register team'() {
        given: 'team information'
            String teamName = 'teamName'
            InetSocketAddress remoteAddress = new InetSocketAddress(InetAddress.getLocalHost(), 8080)
        when: 'team is registered'
            registerTeam(teamName, remoteAddress)
        then: 'team is persisted'
            List<Team> persistedTeams = teamRepository.findAll().collectList().block()
            persistedTeams.size() == 1
            persistedTeams.first().getName() == teamName
            persistedTeams.first().getRemoteAddress() == remoteAddress
    }

    def 'should be able to update team using secret'() {
        when: 'team is registered'
            RegisteredTeam registeredTeam = registerTeam('teamName')
        and: 'team update is created with new IP'
            TeamUpdate teamUpdate = new TeamUpdate(registeredTeam.name, new InetSocketAddress('192.168.1.1', 8080),
                    new ValidTeamSecret(registeredTeam.secret))
        and: 'team is updated'
            registrationFacade.update(teamUpdate).block()
        then:
            List<Team> persistedTeams = teamRepository.findAll().collectList().block()
            persistedTeams.size() == 1
            persistedTeams.first().getName() == registeredTeam.name
            persistedTeams.first().getRemoteAddress() == teamUpdate.remoteAddress
    }

    def 'should not be able to update team without secret'() {
        when: 'team is registered'
            RegisteredTeam registeredTeam = registerTeam('teamName')
        and: 'team update is created with new IP'
            TeamUpdate teamUpdate = new TeamUpdate(registeredTeam.name, new InetSocketAddress('192.168.1.1', 8080), secret as TeamSecret)
        and: 'team is updated'
            registrationFacade.update(teamUpdate).block()
        then:
            thrown(TeamNotFoundException)
        where:
            secret << [new ValidTeamSecret('NOT MY SECRET'), TeamSecret.empty()]
    }

    def 'should include health status of a team'() {
        given: 'team is registered'
            RegisteredTeam registeredTeam = registerTeam('teamName')
        when:
            RegisteredTeam team = registrationFacade.getTeamByName(registeredTeam.name).block()
        then:
            team.health == HealthCheckMonitor.INITIAL_HEALTH_STATUS
    }

    private RegisteredTeam registerTeam(String teamName, InetSocketAddress inetSocketAddress = new InetSocketAddress(InetAddress.getLocalHost(), 8080)) {
        TeamRegistration teamRegistration = new TeamRegistration(teamName, inetSocketAddress)
        registrationFacade.register(teamRegistration).block()
    }
}
