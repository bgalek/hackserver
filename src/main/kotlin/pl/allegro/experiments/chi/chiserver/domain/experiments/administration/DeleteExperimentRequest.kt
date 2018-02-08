package pl.allegro.experiments.chi.chiserver.domain.experiments.administration

import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentId

data class DeleteExperimentRequest(val experimentId: ExperimentId)