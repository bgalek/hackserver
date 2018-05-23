package pl.allegro.experiments.chi.chiserver.domain.statistics.bayes;


import com.google.common.base.Preconditions;

/**
 * Raw data received from PySpark
 */
public class BayesianExperimentStatisticsForVariant {
    private final String experimentId;
    private final String toDate;
    private final String device;
    private final String variantName;
    private final VariantBayesianStatistics data;

    public BayesianExperimentStatisticsForVariant(String experimentId, String toDate, String device, String variantName, VariantBayesianStatistics data) {
        Preconditions.checkNotNull(experimentId, "experimentId cannot be null");
        Preconditions.checkNotNull(toDate, "toDate cannot be null");
        Preconditions.checkNotNull(device, "device cannot be null");
        Preconditions.checkNotNull(data, "data cannot be null");

        this.experimentId = experimentId;
        this.toDate = toDate;
        this.device = device;
        this.variantName = variantName;
        this.data = data;
    }

    public String getExperimentId() {
        return experimentId;
    }

    public String getToDate() {
        return toDate;
    }

    public String getDevice() {
        return device;
    }

    public String getVariantName() {
        return variantName;
    }

    public VariantBayesianStatistics getData() {
        return data;
    }
}
