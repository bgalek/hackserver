package pl.allegro.experiments.chi.chiserver.domain.experiments.administration

import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentId

data class StartExperimentRequest(var experimentId: ExperimentId, var experimentDurationDays: Long)