package pl.allegro.experiments.chi.chiserver.domain.experiments.administration;

public class ExperimentNotFoundException extends RuntimeException {
    ExperimentNotFoundException(String message) {
        super(message);
    }
}