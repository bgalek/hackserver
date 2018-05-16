package pl.allegro.experiments.chi.chiserver.domain.statistics.bayes;

import com.google.common.base.Preconditions;
import pl.allegro.experiments.chi.chiserver.domain.experiments.DeviceClass;

public abstract class BayesianBoxChart {
    private final String experimentId;
    private final DeviceClass deviceClass;
    private final double boxSize;

    public BayesianBoxChart(String experimentId, DeviceClass deviceClass, double boxSize) {
        Preconditions.checkNotNull(experimentId);
        Preconditions.checkNotNull(deviceClass);
        Preconditions.checkArgument(boxSize > 0);
        this.experimentId = experimentId;
        this.deviceClass = deviceClass;
        this.boxSize = boxSize;
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
