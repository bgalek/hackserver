package pl.allegro.experiments.chi.chiserver.domain.experiments.administration;

public class ExperimentNotFoundException extends RuntimeException {
    public ExperimentNotFoundException(String message) {
        super(message);
    }

    public ExperimentNotFoundException(String message, Exception cause) {
        super(message, cause);
    }
}
