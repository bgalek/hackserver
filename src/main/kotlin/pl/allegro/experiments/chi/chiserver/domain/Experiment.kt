package pl.allegro.experiments.chi.chiserver.domain

import java.time.ZonedDateTime


data class Experiment(val id: String,
                      val variants: List<ExperimentVariant>,
                      val activeFrom: ZonedDateTime?,
                      val activeTo: ZonedDateTime?)
