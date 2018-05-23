package pl.allegro.experiments.chi.chiserver.domain.statistics.bayes;

import com.google.common.base.Preconditions;
import pl.allegro.experiments.chi.chiserver.domain.experiments.DeviceClass;

public class BayesianChartMetadata {
    private final String experimentId;
    private final DeviceClass deviceClass;
    private final double boxSize;
    private final String toDate;

    public BayesianChartMetadata(BayesianExperimentStatistics statistics, double boxSize) {
        this(statistics.getExperimentId(), DeviceClass.fromString(statistics.getDevice()), boxSize, statistics.getToDate());
    }

    public BayesianChartMetadata(String experimentId, DeviceClass deviceClass, double boxSize, String toDate) {
        Preconditions.checkNotNull(experimentId);
        Preconditions.checkNotNull(deviceClass);
        Preconditions.checkArgument(boxSize > 0);
        this.experimentId = experimentId;
        this.deviceClass = deviceClass;
        this.boxSize = boxSize;
        this.toDate = toDate;
    }

    public String getToDate() {
        return toDate;
    }

    public String getExperimentId() {
        return experimentId;
    }

    public DeviceClass getDeviceClass() {
        return deviceClass;
    }

    public double getBoxSize() {
        return boxSize;
    }
}
