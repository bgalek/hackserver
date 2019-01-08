package pl.allegro.tech.leaders.hackathon.challenge.api;

import reactor.core.publisher.Mono;

import java.net.URI;

public interface SolutionClient {
    Mono<String> execute(URI uri);
}
