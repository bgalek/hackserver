package pl.allegro.tech.leaders.hackathon.challenge.base

import groovy.transform.CompileStatic
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@CompileStatic
class ReactorValueExtractor {

    static <T> T extract(Mono<T> value) {
        return value.block()
    }

    static <T> List<T> extract(Flux<T> values) {
        return values
                .collectList()
                .block()
    }

    static <T> boolean isEmpty(Mono<T> mono) {
        return mono.blockOptional()
                .map({ false })
                .orElse(true)
    }
}
