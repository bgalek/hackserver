package pl.allegro.experiments.chi.chiserver.domain.statistics;

import com.google.common.base.Preconditions;
import pl.allegro.experiments.chi.chiserver.domain.experiments.DeviceClass;

import java.math.BigDecimal;
import java.util.ArrayList;


public class BayesianHorizontalEqualizer extends BayesianBoxChart {
    private final EqualizerBar joinedBar;

    public BayesianHorizontalEqualizer(BayesianVerticalEqualizer verticalEqualizer) {
        this(verticalEqualizer.getExperimentId(), verticalEqualizer.getDeviceClass(),
             verticalEqualizer.getBoxSize(), toJoinedBar(verticalEqualizer));
    }

    private static EqualizerBar toJoinedBar(BayesianVerticalEqualizer verticalEqualizer) {
        var improvingProbabilities = new ArrayList<BigDecimal>();
        var worseningProbabilities = new ArrayList<BigDecimal>();

        for (int i=0; i<verticalEqualizer.getBoxRadius(); i++) {
            final int idx = i;

            improvingProbabilities.add(
                verticalEqualizer.getBars().stream()
                             .map(b -> b.getImprovingProbabilities().get(idx))
                             .max(BigDecimal::compareTo).orElse(BigDecimal.ZERO)
            );

            worseningProbabilities.add(
                verticalEqualizer.getBars().stream()
                            .map(b -> b.getWorseningProbabilities().get(idx))
                            .min(BigDecimal::compareTo).orElse(BigDecimal.ZERO)
            );

        }

        return new EqualizerBar(null, improvingProbabilities, worseningProbabilities);
    }

    BayesianHorizontalEqualizer(String experimentId, DeviceClass deviceClass, double boxSize, EqualizerBar joinedBar) {
        super(experimentId, deviceClass, boxSize);
        Preconditions.checkNotNull(joinedBar);
        this.joinedBar = joinedBar;
    }

    public EqualizerBar getJoinedBar() {
        return joinedBar;
    }

    public int getBoxRadius() {
        return joinedBar.getImprovingProbabilities().size();
    }
}
