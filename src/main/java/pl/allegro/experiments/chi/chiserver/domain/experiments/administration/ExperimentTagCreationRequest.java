package pl.allegro.experiments.chi.chiserver.domain.experiments.administration;

import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentTag;

public class ExperimentTagCreationRequest {
    private final String experimentTagId;

    public ExperimentTagCreationRequest(String experimentTagId) {
        this.experimentTagId = experimentTagId;
    }

    public String getExperimentTagId() {
        return experimentTagId;
    }

    public ExperimentTag toExperimentTag() {
        return new ExperimentTag(experimentTagId);
    }
}
