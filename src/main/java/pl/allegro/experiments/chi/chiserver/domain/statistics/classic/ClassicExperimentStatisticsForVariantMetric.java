package pl.allegro.experiments.chi.chiserver.domain.statistics.classic;


import pl.allegro.experiments.chi.chiserver.domain.experiments.DeviceClass;

/**
 * Raw data received from PySpark
 */
public class ClassicExperimentStatisticsForVariantMetric {
    private final String experimentId;
    private final long durationMillis;
    private final String toDate;
    private final DeviceClass device;
    private final String variantName;
    private final String metricName;
    private final VariantStatistics data;
    private int version;

    public ClassicExperimentStatisticsForVariantMetric(
            String experimentId,
            long durationMillis,
            String toDate,
            DeviceClass device,
            String variantName,
            String metricName,
            VariantStatistics data) {
        this.experimentId = experimentId;
        this.durationMillis = durationMillis;
        this.toDate = toDate;
        this.device = device;
        this.variantName = variantName;
        this.metricName = metricName;
        this.data = data;
    }

    public String getExperimentId() {
        return experimentId;
    }

    public long getDurationMillis() {
        return durationMillis;
    }

    public String getToDate() {
        return toDate;
    }

    public DeviceClass getDevice() {
        return device;
    }

    public String getVariantName() {
        return variantName;
    }

    public VariantStatistics getData() {
        return data;
    }

    public String getMetricName() {
        return metricName;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getVersion() {
        return version;
    }
}
