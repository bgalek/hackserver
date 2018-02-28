package pl.allegro.experiments.chi.chiserver.domain.statistics;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Map;

public class ExperimentStatistics {
    private final String experimentId;
    private final LocalDate toDate;
    private final Duration duration;
    private final String device;
    private final Map<String, Map<String, VariantStatistics>> metrics;

    public ExperimentStatistics(
            String experimentId,
            LocalDate toDate,
            Duration duration,
            String device,
            // metricName  variantName
            Map<String, Map<String, VariantStatistics>> metrics) {
        Preconditions.checkNotNull(experimentId);
        Preconditions.checkNotNull(toDate);
        Preconditions.checkNotNull(duration);
        Preconditions.checkNotNull(device);
        Preconditions.checkNotNull(metrics);
        this.experimentId = experimentId;
        this.toDate = toDate;
        this.duration = duration;
        this.device = device;
        this.metrics = ImmutableMap.copyOf(metrics);
    }

    public String getExperimentId() {
        return experimentId;
    }

    public LocalDate getToDate() {
        return toDate;
    }

    public Duration getDuration() {
        return duration;
    }

    public String getDevice() {
        return device;
    }

    public Map<String, Map<String, VariantStatistics>> getMetrics() {
        return metrics;
    }
}
