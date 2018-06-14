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
    private int version;

    public BayesianExperimentStatisticsForVariant(String experimentId, String toDate, DeviceClass device, String variantName, VariantBayesianStatistics data) {
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

    public DeviceClass getDevice() {
        return device;
    }

    public String getVariantName() {
        return variantName;
    }

    public VariantBayesianStatistics getData() {
        return data;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getVersion() {
        return version;
    }
}
