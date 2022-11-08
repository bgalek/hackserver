package pl.allegro.tech.leaders.hackathon.registration;

import org.slf4j.Logger;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;
import pl.allegro.tech.leaders.hackathon.registration.api.HealthCheckClient;
import pl.allegro.tech.leaders.hackathon.registration.api.HealthStatus;
import pl.allegro.tech.leaders.hackathon.registration.api.TeamUpdatedEvent;
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
    private final ApplicationEventPublisher applicationEventPublisher;
    private final Map<String, HealthStatus> healthMap = new ConcurrentHashMap<>();

    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(HealthCheckMonitor.class);
    private static final HealthStatus INITIAL_HEALTH_STATUS = UNKNOWN;
    private static final String HEALTH_PROTOCOL = "https";
    private static final String HEALTH_ENDPOINT = "status/health";

    HealthCheckMonitor(HealthCheckClient healthCheckClient, ApplicationEventPublisher applicationEventPublisher) {
        this.healthCheckClient = healthCheckClient;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    HealthStatus getTeamStatus(Team team) {
        return healthMap.getOrDefault(team.getId(), INITIAL_HEALTH_STATUS);
    }

    public Mono<HealthStatus> updateHealth(Team team) {
        URI healthTarget = HealthCheckMonitor.getHealthTarget(team);
        return healthCheckClient
                .execute(healthTarget)
                .map(ResponseEntity::getStatusCode)
                .onErrorResume(throwable -> {
                    logger.info("error during health check: {}", throwable.getMessage());
                    return Mono.just(HttpStatus.SERVICE_UNAVAILABLE);
                })
                .map(httpStatus -> httpStatus.is2xxSuccessful() ? HEALTHY : DEAD)
                .doOnNext(healthStatus -> {
                    healthMap.put(team.getId(), healthStatus);
                    applicationEventPublisher.publishEvent(new TeamUpdatedEvent(team.getName(), healthStatus));
                });
    }

    private static URI getHealthTarget(Team registeredTeam) {
        return UriComponentsBuilder
                .newInstance()
                .scheme(HEALTH_PROTOCOL)
                .host(registeredTeam.getRemoteAddress())
                .port(443)
                .path(HEALTH_ENDPOINT)
                .build()
                .toUri();
    }
}
