package pl.allegro.experiments.chi.chiserver.domain.statistics.classic;

import pl.allegro.experiments.chi.chiserver.domain.experiments.DeviceClass;

import java.util.List;

public interface ClassicStatisticsForVariantMetricRepository {
    List<ClassicExperimentStatisticsForVariantMetric> getLatestForAllMetricsAndVariants(String experimentId, DeviceClass device);

    int countNumberOfTests(String experimentId);

    void save(ClassicExperimentStatisticsForVariantMetric experimentStatistics);
}
