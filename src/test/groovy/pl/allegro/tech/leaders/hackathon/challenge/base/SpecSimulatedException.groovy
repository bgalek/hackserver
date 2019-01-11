package pl.allegro.tech.leaders.hackathon.challenge.base

import reactor.core.publisher.Mono

/**
 * Use this exception to simulate exceptional situations in tests.
 * Thanks to it it will be easier to distinguish real problems from the simulated ones.
 */
class SpecSimulatedException extends RuntimeException {
    static final String DEFAULT_MESSAGE = "Simulated problem for test purposes"

    static <T> Mono<T> asMono(String message = DEFAULT_MESSAGE) {
        return Mono.error(new SpecSimulatedException(message))
    }

    SpecSimulatedException(String message = DEFAULT_MESSAGE) {
        super(message)
    }
}
