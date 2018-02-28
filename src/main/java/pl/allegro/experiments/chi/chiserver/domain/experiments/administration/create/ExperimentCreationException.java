package pl.allegro.experiments.chi.chiserver.domain.experiments.administration.create;

import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.ExperimentCommandException;

public class ExperimentCreationException extends ExperimentCommandException {
    ExperimentCreationException(String message, Exception cause) {
        super(message, cause);
    }

    ExperimentCreationException(String message) {
        super(message);
    }
}