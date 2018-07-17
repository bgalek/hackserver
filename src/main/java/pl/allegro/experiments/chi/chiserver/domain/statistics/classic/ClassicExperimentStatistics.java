package pl.allegro.experiments.chi.chiserver.domain.statistics.classic;

import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.allegro.experiments.chi.chiserver.domain.experiments.DeviceClass;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public class ClassicExperimentStatistics {
    private static final Logger logger = LoggerFactory.getLogger(ClassicExperimentStatistics.class);

    private final String experimentId;
    private final LocalDate toDate;
    private final Duration duration;
    private final DeviceClass device;
    //                metric      variant
    private final Map<String, Map<String, VariantStatistics>> metrics;

    public ClassicExperimentStatistics(String experimentId,
                                       LocalDate toDate,
                                       Duration duration,
                                       DeviceClass device,
                                       Map<String, Map<String, VariantStatistics>> metrics) {
        Preconditions.checkNotNull(experimentId, "experimentId cannot be null");
        Preconditions.checkNotNull(toDate, "toDate cannot be null");
        Preconditions.checkNotNull(device, "device cannot be null");
        Preconditions.checkNotNull(metrics, "metrics cannot be null");

        this.experimentId = experimentId;
        this.toDate = toDate;
        this.duration = duration;
        this.device = device;
        this.metrics = Map.copyOf(validateStatistics(metrics));
    }

    private Map<String, Map<String, VariantStatistics>> validateStatistics(Map<String, Map<String, VariantStatistics>> variantClassicStatistics) {
        Set<String> expectedVariants = null;
        for (Map<String, VariantStatistics> value: variantClassicStatistics.values()) {
            if (expectedVariants == null) {
                expectedVariants = value.keySet();
                continue;
            }
            if (!expectedVariants.equals(value.keySet())) {
                logger.error("Corrupted classic statistics data for {} {}, variant names must match across all metrics", experimentId, device);
                variantClassicStatistics.forEach((metric, stats) -> logger.error("- {} {} {} {} {} {}", experimentId, device, toDate, metric, stats.keySet()));
                throw new RuntimeException("Corrupted classic statistics data for " + experimentId + " " + device);
            }
        }
        return variantClassicStatistics;
    }

    Map<String, VariantStatistics> getMetricStatistics(String metricName) {
        return metrics.get(metricName);
    }

    Stream<VariantStatistics> getAllStatistics() {
        return this.metrics.values().stream().flatMap(stats -> stats.values().stream());
    }

    public String getExperimentId() {
        return experimentId;
    }

    public DeviceClass getDevice() {
        return device;
    }

    public Duration getDuration() {
        return duration;
    }

    public LocalDate getToDate() {
        return toDate;
    }

    public Map<String, Map<String, VariantStatistics>> getMetrics() {
        return metrics;
    }

    Set<String> getVariants() {
        if (metrics.isEmpty()) {
            return Set.of();
        }
        return metrics.values().iterator().next().keySet();
    }

    @Override
    public String toString() {
        return "ClassicExperimentStatistics {" +
                "experimentId='" + experimentId + '\'' +
                ", toDate=" + toDate +
                ", device='" + device + '\'' +
                ", metrics=/map of " + metrics.size() + "/" +
                '}';
    }
}

