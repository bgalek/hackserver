package pl.allegro.experiments.chi.chiserver.domain.experiments.administration.prolong;

import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.ExperimentCommandException;

public class ProlongExperimentException extends ExperimentCommandException {
    ProlongExperimentException(String message) {
        super(message);
    }
}