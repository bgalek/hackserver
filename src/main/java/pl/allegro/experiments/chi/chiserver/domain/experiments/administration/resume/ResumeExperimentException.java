package pl.allegro.experiments.chi.chiserver.domain.experiments.administration.resume;

import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.ExperimentCommandException;

public class ResumeExperimentException extends ExperimentCommandException {
    ResumeExperimentException(String message) {
        super(message);
    }
}