package pl.allegro.experiments.chi.chiserver.domain.experiments.administration.stop;

import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.ExperimentCommandException;

public class StopExperimentException extends ExperimentCommandException {
    StopExperimentException(String message) {
        super(message);
    }
}
