package pl.allegro.experiments.chi.chiserver.domain.experiments.administration.stop;

public class StopExperimentException extends RuntimeException {
    public StopExperimentException(String message, Exception cause) {
        super(message, cause);
    }

    public StopExperimentException(String message) {
        super(message);
    }
}
