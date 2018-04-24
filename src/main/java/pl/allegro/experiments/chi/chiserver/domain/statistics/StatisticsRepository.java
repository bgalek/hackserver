package pl.allegro.experiments.chi.chiserver.domain.statistics;

import java.time.LocalDate;

public interface StatisticsRepository {
    ExperimentStatistics experimentStatistics(String experimentId, LocalDate localDate, String device);

    LocalDate lastStatisticsDate(String experimentId);

    boolean hasAnyStatistics(String experimentId);
}
