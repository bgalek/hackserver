package pl.allegro.experiments.chi.chiserver.domain.experiments.administration.pause;

import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.ExperimentCommandException;

public class PauseExperimentException extends ExperimentCommandException {
    PauseExperimentException(String message) {
        super(message);
    }
}