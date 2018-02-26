package pl.allegro.experiments.chi.chiserver.domain.experiments.administration.create;

public class ExperimentCreationException extends RuntimeException {
    ExperimentCreationException(String message, Exception cause) {
        super(message, cause);
    }

    ExperimentCreationException(String message) {
        super(message);
    }
}