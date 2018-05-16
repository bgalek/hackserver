package pl.allegro.experiments.chi.chiserver.domain.statistics;

import com.google.common.base.Preconditions;
import pl.allegro.experiments.chi.chiserver.domain.experiments.DeviceClass;
import pl.allegro.experiments.chi.chiserver.domain.statistics.bayes.BayesianExperimentStatistics;
import pl.allegro.experiments.chi.chiserver.domain.statistics.bayes.VariantBayesianStatistics;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

public class BayesianVerticalEqualizer extends BayesianBoxChart {
    private final List<EqualizerBar> bars;

    private static final double BOX_SIZE = 0.01;
    private static final int MAX_BOX_RADIUS = 5;

    public BayesianVerticalEqualizer(BayesianExperimentStatistics experimentStatistics) {

        this(experimentStatistics.getExperimentId(),
            DeviceClass.fromString(experimentStatistics.getDevice()),
            BOX_SIZE,
            experimentStatistics.getVariantBayesianStatistics().stream()
                        .map(BayesianVerticalEqualizer::toBar)
                        .collect(Collectors.toList()));
    }

    BayesianVerticalEqualizer(String experimentId, DeviceClass deviceClass, double boxSize, List<EqualizerBar> bars) {
        super(experimentId, deviceClass, boxSize);
        Preconditions.checkNotNull(bars);
        this.bars = List.copyOf(bars);
    }

    private static EqualizerBar toBar(VariantBayesianStatistics statistics) {
        return new EqualizerBar(statistics.getVariantName(),
                statistics.calculateImprovingProbabilities(BOX_SIZE, MAX_BOX_RADIUS),
                statistics.calculateWorseningProbabilities(BOX_SIZE, MAX_BOX_RADIUS));
    }

    private static BigDecimal toRatio(int sampleCount, int allCount) {
        return BigDecimal.valueOf(sampleCount / (double) allCount).setScale(4, RoundingMode.HALF_DOWN);
    }

    public int getBoxRadius() {
        return bars.get(0).getImprovingProbabilities().size();
    }

    public List<EqualizerBar> getBars() {
        return bars;
    }
}
