package pl.allegro.tech.leaders.hackathon.registration.infrastructure.client;

import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import pl.allegro.tech.leaders.hackathon.registration.api.HealthCheckClient;
import reactor.core.publisher.Mono;

import java.net.URI;

class HttpHealthCheckClient implements HealthCheckClient {

    private WebClient webClient = WebClient.builder().build();

    @Override
    public Mono<ResponseEntity<Void>> execute(URI uri) {
        return webClient
                .get()
                .uri(uri)
                .exchange()
                .flatMap(response -> response.toEntity(Void.class));
    }
}
