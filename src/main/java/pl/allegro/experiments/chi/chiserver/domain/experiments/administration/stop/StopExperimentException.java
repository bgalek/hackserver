package pl.allegro.experiments.chi.chiserver.domain.experiments.administration.stop;

public class StopExperimentException extends RuntimeException {
    StopExperimentException(String message, Exception cause) {
        super(message, cause);
    }

    StopExperimentException(String message) {
        super(message);
    }
}
