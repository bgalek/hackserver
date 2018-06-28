package pl.allegro.experiments.chi.chiserver.domain.statistics.clasic;

import pl.allegro.experiments.chi.chiserver.domain.experiments.DeviceClass;

import java.util.List;

public interface ClassicStatisticsForVariantMetricRepository {
    List<ClassicExperimentStatisticsForVariantMetric> getLatestForAllMetricsAndVariants(String experimentId, DeviceClass device);

    void save(ClassicExperimentStatisticsForVariantMetric experimentStatistics);
}
