package pl.allegro.experiments.chi.chiserver.application.administration

import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.create.ExperimentCreationRequest

class CommandTestUtils {
    static def simpleExperimentRequest(String id) {
        def variantNames = []
        def internalVariantName = "v1"
        return new ExperimentCreationRequest(id, variantNames, internalVariantName, null, null, "simple description", "some link", ["group a", "group b"], true)
    }
}