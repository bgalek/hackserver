package pl.allegro.experiments.chi.chiserver.domain.experiments.administration.delete;

public class DeleteExperimentException extends RuntimeException {
    public DeleteExperimentException(String message, Exception cause) {
        super(message, cause);
    }

    public DeleteExperimentException(String message) {
        super(message);
    }
}
