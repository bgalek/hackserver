package pl.allegro.experiments.chi.chiserver.domain.experiments.administration.delete;

public class DeleteExperimentException extends RuntimeException {
    DeleteExperimentException(String message, Exception cause) {
        super(message, cause);
    }
}