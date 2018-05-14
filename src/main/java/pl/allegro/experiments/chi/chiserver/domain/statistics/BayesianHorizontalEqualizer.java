package pl.allegro.experiments.chi.chiserver.domain.statistics;

import com.google.common.base.Preconditions;
import pl.allegro.experiments.chi.chiserver.domain.experiments.DeviceClass;
import pl.allegro.experiments.chi.chiserver.domain.statistics.bayes.BayesianExperimentStatistics;


public class BayesianHorizontalEqualizer extends BayesianEqualizer{
    private final EqualizerBar joinedBar;

    public BayesianHorizontalEqualizer(BayesianExperimentStatistics experimentStatistics) {
        //TODO
        this(experimentStatistics.getExperimentId(), null, 1, null);
    }

    BayesianHorizontalEqualizer(String experimentId, DeviceClass deviceClass, double boxSize, EqualizerBar joinedBar) {
        super(experimentId, deviceClass, boxSize);
        Preconditions.checkNotNull(joinedBar);
        this.joinedBar = joinedBar;
    }

    public EqualizerBar getJoinedBar() {
        return joinedBar;
    }
}
