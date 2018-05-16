package pl.allegro.experiments.chi.chiserver.domain.statistics.bayes;

import com.google.common.base.Preconditions;
import pl.allegro.experiments.chi.chiserver.domain.experiments.DeviceClass;

import java.math.BigDecimal;
import java.util.ArrayList;

public class BayesianHorizontalEqualizer {
    private final BayesianChartMetadata metadata;
    private final EqualizerBar joinedBar;

    public BayesianHorizontalEqualizer(BayesianVerticalEqualizer verticalEqualizer) {
        this(new BayesianChartMetadata(verticalEqualizer.getMetadata().getExperimentId(), verticalEqualizer.getMetadata().getDeviceClass(),
             verticalEqualizer.getMetadata().getBoxSize()), toJoinedBar(verticalEqualizer));
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

    BayesianHorizontalEqualizer(BayesianChartMetadata metadata, EqualizerBar joinedBar) {
        Preconditions.checkNotNull(joinedBar);
        this.metadata = metadata;
        this.joinedBar = joinedBar;
    }

    BayesianHorizontalEqualizer(String experimentId, DeviceClass deviceClass, double boxSize, EqualizerBar joinedBar) {
        this(new BayesianChartMetadata(experimentId, deviceClass, boxSize), joinedBar);
    }

    public EqualizerBar getJoinedBar() {
        return joinedBar;
    }

    public int getBoxRadius() {
        return joinedBar.getImprovingProbabilities().size();
    }

    public BayesianChartMetadata getMetadata() {
        return metadata;
    }
}
