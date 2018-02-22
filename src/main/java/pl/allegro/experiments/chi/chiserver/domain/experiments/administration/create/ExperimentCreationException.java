package pl.allegro.experiments.chi.chiserver.domain.experiments.administration.create;

public class ExperimentCreationException extends RuntimeException {
    public ExperimentCreationException(String message, Exception cause) {
        super(message, cause);
    }

    public ExperimentCreationException(String message) {
        super(message);
    }
}
