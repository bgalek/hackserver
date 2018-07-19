package pl.allegro.experiments.chi.chiserver.application.administration

import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.ExperimentCreationRequest

class CommandTestUtils {
    static def simpleExperimentRequest(String id) {
        def variantNames = []
        def internalVariantName = "v1"
        return new ExperimentCreationRequest(
                id,
                variantNames,
                internalVariantName,
                1,
                null,
                "simple description",
                "some link",
                ["group a", "group b"],
                true,
                null,
                null,
                null)
    }
}