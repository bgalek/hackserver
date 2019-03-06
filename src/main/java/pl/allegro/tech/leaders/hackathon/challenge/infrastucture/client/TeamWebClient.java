package pl.allegro.tech.leaders.hackathon.challenge.infrastucture.client;

import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import pl.allegro.tech.leaders.hackathon.challenge.api.TeamClient;
import reactor.core.publisher.Mono;

import java.net.URI;

class TeamWebClient implements TeamClient {
    private final WebClient webClient;

    TeamWebClient(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public Mono<ResponseEntity<String>> execute(URI uri) {
        return webClient.get()
                .uri(uri)
                .exchange()
                .flatMap(response -> response.toEntity(String.class));
    }
}
