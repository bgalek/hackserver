package pl.allegro.experiments.chi.chiserver.domain.statistics.bayes;

import com.google.common.base.Preconditions;
import com.google.common.collect.Streams;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BayesianHistograms {
    private final BayesianChartMetadata metadata;
    private final List<VariantHistogram> histograms;

    //admin app needs it
    private final String metricName;

    private static final double BOX_SIZE = 0.002;

    public BayesianHistograms(BayesianExperimentStatistics experimentStatistics) {
        this.metadata = new BayesianChartMetadata(experimentStatistics,BOX_SIZE);
        this.histograms = experimentStatistics.getVariantBayesianStatistics().stream()
                .map(BayesianHistograms::toHistogram)
                .collect(Collectors.toList());
        this.metricName = experimentStatistics.getMetricName();
    }

    private static VariantHistogram toHistogram(VariantBayesianStatistics rawStatistics) {
        Preconditions.checkArgument(rawStatistics != null);

        var statistics = rawStatistics.noiseReduction();


        var frequencies = statistics.calculateFrequencies();
        var improvingLabels = statistics.calculateImprovingProbabilities(BOX_SIZE, statistics.radius());
        var worseningLabels = statistics.calculateWorseningProbabilities(BOX_SIZE, statistics.radius());

        Stream<BigDecimal> allLabels = Streams.concat(worseningLabels.stream(), improvingLabels.stream());

        return new VariantHistogram(
                statistics.getVariantName(),
                statistics.getSamples().getValues(),
                frequencies,
                allLabels
                        .map(it -> it.multiply(BigDecimal.valueOf(100)).setScale(1, RoundingMode.HALF_DOWN))
                        .collect(Collectors.toList())
                );
    }

    public List<VariantHistogram> getVariantHistograms() {
        return histograms;
    }

    public BayesianChartMetadata getMetadata() {
        return metadata;
    }
}
