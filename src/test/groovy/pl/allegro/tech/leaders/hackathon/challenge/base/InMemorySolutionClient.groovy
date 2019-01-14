package pl.allegro.tech.leaders.hackathon.challenge.base

import pl.allegro.tech.leaders.hackathon.challenge.api.SolutionClient
import reactor.core.publisher.Mono

class InMemorySolutionClient implements SolutionClient {
    Map<URI, Mono<String>> recorderResponses = new HashMap<>()

    @Override
    Mono<String> execute(URI uri) {
        return recorderResponses.get(uri)
    }

    InMemorySolutionClient recordResponse(URI uri, Mono<String> response) {
        recorderResponses.put(uri, response)
        return this
    }
}
