package pl.allegro.experiments.chi.chiserver.domain.statistics;

import com.google.common.base.Preconditions;
import pl.allegro.experiments.chi.chiserver.domain.experiments.DeviceClass;

public class BayesianHorizontalEqualizer extends BayesianEqualizer{
    private final EqualizerBar joinedBar;

    public BayesianHorizontalEqualizer(String experimentId, DeviceClass deviceClass, double diffStepSize, EqualizerBar joinedBar) {
        super(experimentId, deviceClass, diffStepSize);
        Preconditions.checkNotNull(joinedBar);
        this.joinedBar = joinedBar;
    }

    public EqualizerBar getJoinedBar() {
        return joinedBar;
    }
}
