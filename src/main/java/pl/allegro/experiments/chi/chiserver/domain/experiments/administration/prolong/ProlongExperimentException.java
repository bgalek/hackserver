package pl.allegro.experiments.chi.chiserver.domain.experiments.administration.prolong;

public class ProlongExperimentException extends RuntimeException {
    ProlongExperimentException(String message, Exception cause) {
        super(message, cause);
    }

    ProlongExperimentException(String message) {
        super(message);
    }
}