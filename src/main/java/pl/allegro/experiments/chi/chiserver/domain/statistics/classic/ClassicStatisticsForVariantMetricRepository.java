package pl.allegro.experiments.chi.chiserver.domain.statistics.classic;

import java.util.List;

public interface ClassicStatisticsForVariantMetricRepository {
    List<ClassicExperimentStatisticsForVariantMetric> getLatestForAllMetricsAndVariants(String experimentId);

    int countNumberOfTests(String experimentId);

    int countNumberExperimentsWithStats();

    void save(ClassicExperimentStatisticsForVariantMetric experimentStatistics);
}