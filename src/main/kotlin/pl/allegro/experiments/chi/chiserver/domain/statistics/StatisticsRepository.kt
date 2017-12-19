package pl.allegro.experiments.chi.chiserver.domain.statistics

import java.time.LocalDate

interface StatisticsRepository {
    fun experimentStatistics(experimentId: ExperimentId, toDate : LocalDate, device: String) : ExperimentStatistics
}