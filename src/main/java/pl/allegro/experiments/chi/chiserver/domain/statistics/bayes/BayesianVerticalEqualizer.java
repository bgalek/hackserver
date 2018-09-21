package pl.allegro.experiments.chi.chiserver.domain.statistics.bayes;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

public class BayesianVerticalEqualizer {
    private final BayesianChartMetadata metadata;
    private final List<EqualizerBar> bars;

    private static final double BOX_SIZE = 0.01;
    private static final int RADIUS = 5;

    public BayesianVerticalEqualizer(BayesianExperimentStatistics experimentStatistics) {
        this(new BayesianChartMetadata(experimentStatistics, BOX_SIZE), experimentStatistics.getVariantBayesianStatistics().stream()
                .map(BayesianVerticalEqualizer::toBar)
                .collect(Collectors.toList()));
    }

    private BayesianVerticalEqualizer(BayesianChartMetadata metadata, List<EqualizerBar> bars) {
        Preconditions.checkNotNull(bars);
        this.metadata = metadata;
        this.bars = List.copyOf(bars);
    }

    private static EqualizerBar toBar(VariantBayesianStatistics statistics) {
        return new EqualizerBar(statistics.getVariantName(),
                statistics.calculateImprovingProbabilities(BOX_SIZE, RADIUS),
                Lists.reverse(statistics.calculateWorseningProbabilities(BOX_SIZE, RADIUS)));
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

    public BayesianChartMetadata getMetadata() {
        return metadata;
    }
}
