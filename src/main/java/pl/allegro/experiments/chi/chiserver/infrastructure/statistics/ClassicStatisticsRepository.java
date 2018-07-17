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

import java.time.Duration;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class ClassicStatisticsRepository implements StatisticsRepository {
    private static final Logger logger = LoggerFactory.getLogger(ClassicStatisticsRepository.class);

    private final ClassicStatisticsForVariantMetricRepository classicStatisticsForVariantMetricRepository;

    @Autowired
    public ClassicStatisticsRepository(ClassicStatisticsForVariantMetricRepository classicStatisticsForVariantMetricRepository) {
        this.classicStatisticsForVariantMetricRepository = classicStatisticsForVariantMetricRepository;
    }

    public Optional<ClassicExperimentStatistics> getExperimentStatistics(String experimentId, DeviceClass device) {
        List<ClassicExperimentStatisticsForVariantMetric> stats =
                classicStatisticsForVariantMetricRepository.getLatestForAllMetricsAndVariants(experimentId, device);

        if (stats.isEmpty()) {
            return Optional.empty();
        }
        Map<String, Map<String, VariantStatistics>> metricStatistics = new HashMap<>();
        for (ClassicExperimentStatisticsForVariantMetric metric: stats) {
            metricStatistics
                    .computeIfAbsent(metric.getMetricName(), metricName -> new HashMap<>())
                    .computeIfAbsent(metric.getVariantName(), variantName -> metric.getData());

        }

        if (! validateDateAndDuration(experimentId, device, stats)) {
            return Optional.empty();
        }

        LocalDate toDate = LocalDate.parse(stats.get(0).getToDate());
        Duration duration = Duration.ofMillis(stats.get(0).getDurationMillis());
        return Optional.of(new ClassicExperimentStatistics(experimentId, toDate, duration, device, metricStatistics));
    }

    private boolean validateDateAndDuration(String experimentId, DeviceClass device, List<ClassicExperimentStatisticsForVariantMetric> stats) {
        Set<String> distinctDates = stats.stream().map(it -> it.getToDate()).collect(Collectors.toSet());
        if (distinctDates.size() != 1) {
            logger.error("Corrupted classic statistics data for {} {}, toDate is not unique", experimentId, device);
            stats.forEach(it -> logger.error("- {} {} {} {} {}", it.getExperimentId(), it.getDevice(), it.getToDate(), it.getVariantName(), it.getMetricName()));
            return false;
        }
        Set<Long> distinctDurations = stats.stream().map(it -> it.getDurationMillis()).collect(Collectors.toSet());
        if (distinctDurations.size() != 1) {
            logger.error("Corrupted classic statistics data for {} {}, duration is not unique", experimentId, device);
            stats.forEach(it -> logger.error("- {} {} {} {} {} {}", it.getExperimentId(), it.getDevice(), it.getToDate(), it.getDurationMillis(), it.getVariantName(), it.getMetricName()));
            return false;
        }
        return true;
    }

    @Override
    public Optional<ClassicExperimentStatistics> getExperimentStatistics(String experimentId, String device) {
        return getExperimentStatistics(experimentId, DeviceClass.fromString(device));
    }

    @Override
    public boolean hasAnyStatistics(String experimentId) {
        return getExperimentStatistics(experimentId, DeviceClass.all).isPresent();
    }
}
