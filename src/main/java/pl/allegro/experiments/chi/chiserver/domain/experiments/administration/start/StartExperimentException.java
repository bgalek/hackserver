package pl.allegro.experiments.chi.chiserver.domain.experiments.administration.start;

public class StartExperimentException extends RuntimeException {
    public StartExperimentException(String message, Exception cause) {
        super(message, cause);
    }

    public StartExperimentException(String message) {
        super(message);
    }
}