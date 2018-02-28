package pl.allegro.experiments.chi.chiserver.domain.experiments.administration.start;

import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.ExperimentCommandException;

public class StartExperimentException extends ExperimentCommandException {
    StartExperimentException(String message) {
        super(message);
    }
}