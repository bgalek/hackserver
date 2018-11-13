package pl.allegro.experiments.chi.chiserver.domain.statistics.classic;

import java.util.List;

public interface StatisticsRepository {
    List<ClassicExperimentStatistics> getExperimentStatistics(String experimentId);

    boolean hasAnyStatistics(String experimentId);
}
