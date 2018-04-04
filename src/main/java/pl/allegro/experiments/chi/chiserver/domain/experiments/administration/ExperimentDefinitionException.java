package pl.allegro.experiments.chi.chiserver.domain.experiments.administration;

public class ExperimentDefinitionException extends RuntimeException {

    public ExperimentDefinitionException(String message) {
        super(message);
    }

    public ExperimentDefinitionException(String message, Throwable cause) {
        super(message, cause);
    }
}
