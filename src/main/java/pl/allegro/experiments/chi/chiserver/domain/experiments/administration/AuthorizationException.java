package pl.allegro.experiments.chi.chiserver.domain.experiments.administration;

public class AuthorizationException extends RuntimeException {
    public AuthorizationException(String message) {
        super(message);
    }
}