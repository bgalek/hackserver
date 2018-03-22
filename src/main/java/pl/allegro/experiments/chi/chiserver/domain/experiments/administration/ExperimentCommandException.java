package pl.allegro.experiments.chi.chiserver.domain.experiments.administration;

public class ExperimentCommandException extends RuntimeException {

    public ExperimentCommandException(String message) {
        super(message);
    }

    public ExperimentCommandException(String message, Throwable cause) {
        super(message, cause);
    }
}
