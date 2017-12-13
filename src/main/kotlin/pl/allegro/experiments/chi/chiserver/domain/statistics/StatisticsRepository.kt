package pl.allegro.experiments.chi.chiserver.domain.statistics

import pl.allegro.experiments.chi.chiserver.domain.experiments.Experiment
import java.time.LocalDate

interface StatisticsRepository {
    fun experimentStatistics(experiment: Experiment, toDate: LocalDate, device: String): ExperimentStatistics

    fun lastStatisticsDate(experiment: Experiment): LocalDate?
}