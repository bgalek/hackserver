package pl.allegro.experiments.chi.chiserver.domain.experiments.administration.delete;

import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.ExperimentCommandException;

public class DeleteExperimentException extends ExperimentCommandException {
    DeleteExperimentException(String message) {
        super(message);
    }
}