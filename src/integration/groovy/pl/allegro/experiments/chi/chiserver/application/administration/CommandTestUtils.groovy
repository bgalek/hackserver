package pl.allegro.experiments.chi.chiserver.application.administration

import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.ExperimentCreationRequest

class CommandTestUtils {
    static def simpleExperimentRequest(String id) {
        def variantNames = ["v2", "v3"]
        def internalVariantName = "v1"
        return ExperimentCreationRequest.builder()
                .id(id)
                .variantNames(variantNames)
                .internalVariantName(internalVariantName)
                .percentage(1)
                .description("simple description")
                .documentLink("some link")
                .groups(["group a", "group b"])
                .reportingEnabled(true)
                .build()
    }
}