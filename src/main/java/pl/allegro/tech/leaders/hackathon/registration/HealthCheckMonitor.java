package pl.allegro.tech.leaders.hackathon.registration;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pl.allegro.tech.leaders.hackathon.registration.api.HealthCheckClient;
import pl.allegro.tech.leaders.hackathon.registration.api.HealthStatus;
import reactor.core.publisher.Mono;

import java.net.InetSocketAddress;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static pl.allegro.tech.leaders.hackathon.registration.api.HealthStatus.DEAD;
import static pl.allegro.tech.leaders.hackathon.registration.api.HealthStatus.HEALTHY;
import static pl.allegro.tech.leaders.hackathon.registration.api.HealthStatus.UNKNOWN;

class HealthCheckMonitor {

    private final HealthCheckClient healthCheckClient;
    private final Map<String, HealthStatus> healthMap = new ConcurrentHashMap<>();

    private static final HealthStatus INITIAL_HEALTH_STATUS = UNKNOWN;
    private static final String HEALTH_ENDPOINT = "status/health";

    HealthCheckMonitor(HealthCheckClient healthCheckClient) {
        this.healthCheckClient = healthCheckClient;
    }

    HealthStatus getTeamStatus(Team team) {
        return healthMap.getOrDefault(team.getId(), INITIAL_HEALTH_STATUS);
    }

    public Mono<HealthStatus> updateHealth(Team team) {
        URI healthTarget = HealthCheckMonitor.getHealthTarget(team);
        return healthCheckClient
                .execute(healthTarget)
                .map(ResponseEntity::getStatusCode)
                .onErrorResume(throwable -> Mono.just(HttpStatus.SERVICE_UNAVAILABLE))
                .map(httpStatus -> httpStatus.is2xxSuccessful() ? HEALTHY : DEAD)
                .doOnNext(healthStatus -> healthMap.put(team.getId(), healthStatus));
    }

    private static URI getHealthTarget(Team registeredTeam) {
        InetSocketAddress remoteAddress = registeredTeam.getRemoteAddress();
        String address = remoteAddress.getAddress().getHostAddress();
        int port = remoteAddress.getPort();
        String target = String.format("http://%s:%d/%s", address, port, HEALTH_ENDPOINT);
        return URI.create(target);
    }
}