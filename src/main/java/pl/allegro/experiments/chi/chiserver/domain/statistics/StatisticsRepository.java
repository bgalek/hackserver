package pl.allegro.experiments.chi.chiserver.domain.statistics;

import pl.allegro.experiments.chi.chiserver.domain.experiments.Experiment;

import java.time.LocalDate;

public interface StatisticsRepository {
    ExperimentStatistics experimentStatistics(Experiment experiment, LocalDate localDate, String device);

    LocalDate lastStatisticsDate(Experiment experiment);

    boolean hasAnyStatistics(Experiment experiment);
}
