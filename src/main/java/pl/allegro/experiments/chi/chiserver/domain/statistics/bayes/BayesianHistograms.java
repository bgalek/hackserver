package pl.allegro.experiments.chi.chiserver.domain.statistics.bayes;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Streams;
import pl.allegro.experiments.chi.chiserver.domain.experiments.DeviceClass;
import pl.allegro.experiments.chi.chiserver.domain.statistics.VariantHistogram;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BayesianHistograms {
    private final BayesianChartMetadata metadata;
    private final List<VariantHistogram> histograms;
    private static final double BOX_SIZE = 0.001;
    private static final int MAX_BOX = 100;

    public BayesianHistograms(BayesianExperimentStatistics experimentStatistics) {
        this(experimentStatistics.getExperimentId(),
                DeviceClass.fromString(experimentStatistics.getDevice()),
                BOX_SIZE,
                experimentStatistics.getVariantBayesianStatistics().stream()
                        .map(BayesianHistograms::toHistogram)
                        .collect(Collectors.toList()));
    }

    private BayesianHistograms(BayesianChartMetadata metadata, List<VariantHistogram> histograms) {
        this.metadata = metadata;
        this.histograms = histograms;
    }

    private BayesianHistograms(String experimentId, DeviceClass deviceClass, double boxSize, List<VariantHistogram> histograms) {
        this(new BayesianChartMetadata(experimentId, deviceClass, boxSize), histograms);

    }

    private static VariantHistogram toHistogram(VariantBayesianStatistics statistics) {
        Preconditions.checkArgument(statistics.getSamples().getValues().size() == MAX_BOX * 2);

        List<BigDecimal> improvingProbabilities = statistics.calculateImprovingProbabilities(BOX_SIZE, MAX_BOX);
        List<BigDecimal> worseningProbabilities = statistics.calculateWorseningProbabilities(BOX_SIZE, MAX_BOX);

        Stream<BigDecimal> allProbabilities = Streams.concat(Lists.reverse(worseningProbabilities).stream(), improvingProbabilities.stream());

        return new VariantHistogram(
                statistics.getVariantName(),
                statistics.getSamples().getValues(),
                statistics.getSamples().getCounts(),
                allProbabilities
                        .map(it -> it.multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_DOWN))
                        .map(it -> it.toString()+"%").collect(Collectors.toList())
                );
    }

    public List<VariantHistogram> getVariantHistograms() {
        return histograms;
    }

    public BayesianChartMetadata getMetadata() {
        return metadata;
    }
}
