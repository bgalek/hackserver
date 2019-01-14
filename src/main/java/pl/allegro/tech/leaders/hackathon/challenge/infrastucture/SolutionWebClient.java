package pl.allegro.tech.leaders.hackathon.challenge.infrastucture;

import org.springframework.web.reactive.function.client.WebClient;
import pl.allegro.tech.leaders.hackathon.challenge.api.SolutionClient;
import reactor.core.publisher.Mono;

import java.net.URI;

import static java.util.Objects.requireNonNull;

// TODO: Register in spring and configure webclient
class SolutionWebClient implements SolutionClient {
    private final WebClient webClient;

    public SolutionWebClient(WebClient webClient) {
        this.webClient = requireNonNull(webClient);
    }

    @Override
    public Mono<String> execute(URI uri) {
        // TODO: Add timeout from config
        return webClient.get()
                .uri(uri)
                .retrieve()
                .bodyToMono(String.class);
    }
}
