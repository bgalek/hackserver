package pl.allegro.experiments.chi.chiserver.domain.statistics;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Map;

public class ExperimentStatistics {
    private final String experimentId;
    private final LocalDate toDate;
    private final Duration duration;
    private final String device;
    private final Map<MetricName, Map<VariantName, VariantStatistics>> metrics;

    public ExperimentStatistics(
            String experimentId,
            LocalDate toDate,
            Duration duration,
            String device,
            Map<MetricName, Map<VariantName, VariantStatistics>> metrics) {
        this.experimentId = experimentId;
        this.toDate = toDate;
        this.duration = duration;
        this.device = device;
        this.metrics = metrics;
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

    public Map<MetricName, Map<VariantName, VariantStatistics>> getMetrics() {
        return metrics;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ExperimentStatistics that = (ExperimentStatistics) o;

        if (!experimentId.equals(that.experimentId)) return false;
        if (!toDate.equals(that.toDate)) return false;
        if (!duration.equals(that.duration)) return false;
        if (!device.equals(that.device)) return false;
        return metrics.equals(that.metrics);
    }

    @Override
    public int hashCode() {
        int result = experimentId.hashCode();
        result = 31 * result + toDate.hashCode();
        result = 31 * result + duration.hashCode();
        result = 31 * result + device.hashCode();
        result = 31 * result + metrics.hashCode();
        return result;
    }
}
