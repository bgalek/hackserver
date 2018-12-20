package pl.allegro.experiments.chi.chiserver.application.scorer;

public class UnauthorizedPublicApiCallException extends RuntimeException {
    UnauthorizedPublicApiCallException() {
        super("Unauthorized public api call");
    }
}
