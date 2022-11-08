package pl.allegro.tech.leaders.hackathon.registration.infrastructure.client;

import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import pl.allegro.tech.leaders.hackathon.registration.api.HealthCheckClient;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.Duration;

class HttpHealthCheckClient implements HealthCheckClient {

    private final WebClient webClient = WebClient.builder().build();

    @Override
    public Mono<ResponseEntity<Void>> execute(URI uri) {
        return webClient
                .get()
                .uri(uri)
                .exchangeToMono(Mono::just)
                .timeout(Duration.ofMillis(2000))
                .flatMap(response -> response.toEntity(Void.class));
    }
}
