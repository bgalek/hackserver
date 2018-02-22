package pl.allegro.experiments.chi.chiserver.domain.experiments.administration.prolong;

public class ProlongExperimentException extends RuntimeException {
    public ProlongExperimentException(String message, Exception cause) {
        super(message, cause);
    }

    public ProlongExperimentException(String message) {
        super(message);
    }
}