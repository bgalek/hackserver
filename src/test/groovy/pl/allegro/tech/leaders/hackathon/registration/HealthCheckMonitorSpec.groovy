package pl.allegro.tech.leaders.hackathon.registration

import org.springframework.http.ResponseEntity
import pl.allegro.tech.leaders.hackathon.registration.api.HealthCheckClient
import pl.allegro.tech.leaders.hackathon.registration.api.HealthStatus
import reactor.core.publisher.Mono
import spock.lang.Unroll

import static org.springframework.http.HttpStatus.OK
import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE
import static pl.allegro.tech.leaders.hackathon.registration.HealthCheckMonitor.HEALTH_ENDPOINT

class HealthCheckMonitorSpec extends RegistrationSpec {

    def 'should generate valid health check url'() {
        given: 'new team'
            Team team = createTeam('127.0.0.1', 3000)
        when: 'team is registered'
            registerTeam(team)
        and: 'health check monitor is created'
            HealthCheckMonitor healthCheckMonitor = new HealthCheckMonitor(new HealthyTeamClient())
        then: 'health checking url is valid for given team'
            healthCheckMonitor.getHealthTarget(team) == URI.create("http://127.0.0.1:3000/$HEALTH_ENDPOINT")
    }

    @Unroll
    def 'should be able to get registered team with it\'s health status (#health)'() {
        given: 'new team and configured health check monitor'
            Team team = createTeam('127.0.0.1', 3000)
            HealthCheckMonitor healthCheckMonitor = new HealthCheckMonitor(client)
        when: 'team is registered'
            registerTeam(team)
        then: 'registration facade returns team initial health state'
            registrationFacade
                    .getTeamById(team.getId()).block()
                    .health == HealthCheckMonitor.INITIAL_HEALTH_STATUS
        when: 'health check is called for this team'
            healthCheckMonitor.updateHealth(team).block()
        then: 'tam healths get updated'
            healthCheckMonitor.getTeamStatus(team) == health
        where:
            client                  || health
            new HealthyTeamClient() || HealthStatus.HEALTHY
            new DeadTeamClient()    || HealthStatus.DEAD
    }

    class HealthyTeamClient implements HealthCheckClient {
        @Override
        Mono<ResponseEntity<Void>> execute(URI uri) {
            Mono.just(new ResponseEntity(null, OK))
        }
    }

    class DeadTeamClient implements HealthCheckClient {
        @Override
        Mono<ResponseEntity<Void>> execute(URI uri) {
            Mono.just(new ResponseEntity(null, SERVICE_UNAVAILABLE))
        }
    }
}
