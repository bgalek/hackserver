package pl.allegro.experiments.chi.chiserver.statistics

import java.time.LocalDate

interface StatisticsRepository {
    fun experimentStatistics(experimentId: ExperimentId, toDate : LocalDate, device: String) : ExperimentStatistics
}