package pl.allegro.experiments.chi.chiserver.domain.experiments.administration.start;

public class StartExperimentException extends RuntimeException {
    StartExperimentException(String message, Exception cause) {
        super(message, cause);
    }
}