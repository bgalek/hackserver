package pl.allegro.experiments.chi.chiserver.application.administration

import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.create.ExperimentCreationRequest

class CommandTestUtils {
    static def simpleExperimentRequest(String id) {
        def variants = [new ExperimentCreationRequest.Variant("v1", [new ExperimentCreationRequest.Predicate(ExperimentCreationRequest.PredicateType.INTERNAL, null, null, null, null)])]
        return new ExperimentCreationRequest(id, variants, "simple description", ["group a", "group b"], true)
    }
}