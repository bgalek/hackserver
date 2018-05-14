package pl.allegro.experiments.chi.chiserver.domain.statistics;

import com.google.common.base.Preconditions;
import pl.allegro.experiments.chi.chiserver.domain.experiments.DeviceClass;

import java.util.List;

public class BayesianVerticalEqualizer extends BayesianEqualizer{
    private final List<EqualizerBar> bars;

    public BayesianVerticalEqualizer(String experimentId, DeviceClass deviceClass, double diffStepSize, List<EqualizerBar> bars) {
        super(experimentId, deviceClass, diffStepSize);
        Preconditions.checkNotNull(bars);
        this.bars = List.copyOf(bars);
    }

    public List<EqualizerBar> getBars() {
        return bars;
    }
}
