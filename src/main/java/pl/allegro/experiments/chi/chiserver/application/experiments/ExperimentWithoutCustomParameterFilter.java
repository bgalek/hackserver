package pl.allegro.experiments.chi.chiserver.application.experiments;

import pl.allegro.experiments.chi.chiserver.domain.experiments.Experiment;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentDefinition;

class ExperimentWithoutCustomParameterFilter {
    static Boolean filter(Experiment experiment) {
        return !experiment.getDefinition().map(ExperimentDefinition::getCustomParameter).isPresent();
    }
}
