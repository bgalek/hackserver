package pl.allegro.experiments.chi.chiserver.domain.statistics;

import java.time.LocalDate;
import java.util.Optional;

public interface StatisticsRepository {
    ExperimentStatistics experimentStatistics(String experimentId, LocalDate localDate, String device);

    Optional<LocalDate> lastStatisticsDate(String experimentId);

    boolean hasAnyStatistics(String experimentId);
}
