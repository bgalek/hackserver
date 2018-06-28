package pl.allegro.experiments.chi.chiserver.domain.statistics.clasic;

import java.util.Optional;

public interface StatisticsRepository {
    Optional<ClassicExperimentStatistics> getExperimentStatistics(String experimentId, String device);

    boolean hasAnyStatistics(String experimentId);
}
