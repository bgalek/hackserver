package pl.allegro.tech.leaders.hackathon.registration.api;

import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

import java.net.URI;

public interface HealthCheckClient {
    Mono<ResponseEntity<Void>> execute(URI uri);
}
