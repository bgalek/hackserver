package pl.allegro.experiments.chi.chiserver.domain.statistics;

import com.google.common.base.Preconditions;
import pl.allegro.experiments.chi.chiserver.domain.experiments.DeviceClass;

public abstract class BayesianEqualizer {
    private final String experimentId;
    private final DeviceClass deviceClass;
    private final double diffStepSize;      //percent

    public BayesianEqualizer(String experimentId, DeviceClass deviceClass, double diffStepSize) {
        Preconditions.checkNotNull(experimentId);
        Preconditions.checkNotNull(deviceClass);
        Preconditions.checkArgument(diffStepSize > 0);
        this.experimentId = experimentId;
        this.deviceClass = deviceClass;
        this.diffStepSize = diffStepSize;
    }

    public String getExperimentId() {
        return experimentId;
    }

    public DeviceClass getDeviceClass() {
        return deviceClass;
    }

    public double getDiffStepSize() {
        return diffStepSize;
    }
}
