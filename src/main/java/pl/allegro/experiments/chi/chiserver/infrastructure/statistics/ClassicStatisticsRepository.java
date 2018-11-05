package pl.allegro.experiments.chi.chiserver.infrastructure.statistics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import pl.allegro.experiments.chi.chiserver.domain.experiments.DeviceClass;
import pl.allegro.experiments.chi.chiserver.domain.statistics.classic.StatisticsRepository;
import pl.allegro.experiments.chi.chiserver.domain.statistics.classic.VariantStatistics;
import pl.allegro.experiments.chi.chiserver.domain.statistics.classic.ClassicExperimentStatistics;
import pl.allegro.experiments.chi.chiserver.domain.statistics.classic.ClassicExperimentStatisticsForVariantMetric;
import pl.allegro.experiments.chi.chiserver.domain.statistics.classic.ClassicStatisticsForVariantMetricRepository;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class ClassicStatisticsRepository implements StatisticsRepository {
    private static final Logger logger = LoggerFactory.getLogger(ClassicStatisticsRepository.class);

    private final ClassicStatisticsForVariantMetricRepository classicStatisticsForVariantMetricRepository;

    @Autowired
    public ClassicStatisticsRepository(ClassicStatisticsForVariantMetricRepository classicStatisticsForVariantMetricRepository) {
        this.classicStatisticsForVariantMetricRepository = classicStatisticsForVariantMetricRepository;
    }

    @Override
    public List<ClassicExperimentStatistics> getExperimentStatistics(String experimentId) {
        List<ClassicExperimentStatisticsForVariantMetric> stats =
                classicStatisticsForVariantMetricRepository.getLatestForAllMetricsAndVariants(experimentId);

        if (!validateDate(experimentId, stats)) {
            return Collections.emptyList();
        }

        // Device       metric       variant
        Map<String, Map<String, Map<String, VariantStatistics>>> metricStatisticsPerDevice = new HashMap<>();
        for (ClassicExperimentStatisticsForVariantMetric metric: stats) {
            metricStatisticsPerDevice
                    .computeIfAbsent(metric.getDevice().toJsonString(), deviceClassName -> new HashMap<>())
                    .computeIfAbsent(metric.getMetricName(), metricName -> new HashMap<>())
                    .computeIfAbsent(metric.getVariantName(), variantName -> metric.getData());

        }
        LocalDate toDate = LocalDate.parse(stats.get(0).getToDate());
        return metricStatisticsPerDevice.keySet().stream().map(deviceClassName ->
                new ClassicExperimentStatistics(
                        experimentId,
                        toDate,
                        DeviceClass.fromString(deviceClassName),
                        metricStatisticsPerDevice.get(deviceClassName))).collect(Collectors.toList());
    }

    private boolean validateDate(String experimentId, List<ClassicExperimentStatisticsForVariantMetric> stats) {
        Set<String> distinctDates = stats.stream().map(it -> it.getToDate()).collect(Collectors.toSet());
        if (distinctDates.size() != 1) {
            logger.error("Corrupted classic statistics data for {}, toDate is not unique", experimentId);
            stats.forEach(it -> logger.error("- {} {} {} {} {}", it.getExperimentId(), it.getDevice(), it.getToDate(), it.getVariantName(), it.getMetricName()));
            return false;
        }
        return true;
    }

    @Override
    public boolean hasAnyStatistics(String experimentId) {
        return !getExperimentStatistics(experimentId).isEmpty();
    }
}
