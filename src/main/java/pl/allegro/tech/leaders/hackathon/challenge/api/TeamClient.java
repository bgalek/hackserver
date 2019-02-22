package pl.allegro.tech.leaders.hackathon.challenge.api;

import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

import java.net.URI;

public interface TeamClient {
    Mono<ResponseEntity<String>> execute(URI uri);
}
