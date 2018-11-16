package pl.allegro.experiments.chi.chiserver.domain.statistics.bayes;

import com.google.common.base.Preconditions;
import pl.allegro.experiments.chi.chiserver.domain.experiments.DeviceClass;

/**
 * Raw data received from PySpark
 */
public class BayesianExperimentStatisticsForVariant {
    private final String experimentId;
    private final String toDate;
    private final DeviceClass device;
    private final String variantName;
    private final VariantBayesianStatistics data;
    private final String metricName;

    public BayesianExperimentStatisticsForVariant(String experimentId, String toDate, DeviceClass device, String variantName, VariantBayesianStatistics data, String metricName) {
        Preconditions.checkNotNull(experimentId, "experimentId can't be null");
        Preconditions.checkNotNull(toDate, "toDate can't be null");
        Preconditions.checkNotNull(device, "device can't be null");
        Preconditions.checkNotNull(data, "data can't be null");

        //Preconditions.checkNotNull(metricName, "metricName can't be null");

        this.experimentId = experimentId;
        this.toDate = toDate;
        this.device = device;
        this.variantName = variantName;
        this.data = data;
        this.metricName = metricName;
    }

    public String getExperimentId() {
        return experimentId;
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

    public VariantBayesianStatistics getData() {
        return data;
    }

    public String getMetricName() {
        return metricName;
    }
}
