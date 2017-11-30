package pl.allegro.experiments.chi.chiserver.statistics

import java.time.Duration
import java.time.LocalDate

typealias ExperimentId = String
typealias MetricName = String
typealias VariantName = String

data class Statistics(val value: Double, val diff: Double, val pValue: Double, val count: Int)

typealias Variants = Map<VariantName, Statistics>

data class ExperimentStatistics(
        val experimentId: ExperimentId,
        val toDate: LocalDate,
        val duration: Duration,
        val device: String,
        val metrics: Map<MetricName, Variants>
)