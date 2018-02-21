package pl.allegro.experiments.chi.chiserver.domain.statistics

import java.time.Duration
import java.time.LocalDate

typealias MetricName = String
typealias VariantName = String

data class VariantStatistics(val value: Double, val diff: Double, val pValue: Double, val count: Int)

typealias Variants = Map<VariantName, VariantStatistics>

data class ExperimentStatistics(
    val experimentId: String,
    val toDate: LocalDate,
    val duration: Duration,
    val device: String,
    val metrics: Map<MetricName, Variants>
)