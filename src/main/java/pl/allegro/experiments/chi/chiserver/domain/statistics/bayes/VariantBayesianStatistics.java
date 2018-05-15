package pl.allegro.experiments.chi.chiserver.domain.statistics.bayes;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class VariantBayesianStatistics {
    private final String variantName;
    private final Samples samples;
    private int outliersLeft;
    private int outliersRight;

    public VariantBayesianStatistics(String variantName,
                                     Samples samples,
                                     Integer outliersLeft,
                                     Integer outliersRight) {
        this.variantName = variantName;
        this.samples = samples;
        this.outliersLeft = outliersLeft == null ? 0 : outliersLeft;
        this.outliersRight = outliersRight == null ? 0 : outliersRight;
    }

    public String getVariantName() {
        return variantName;
    }

    public Samples getSamples() {
        return samples;
    }

    List<Sample> getSamplesAsList() {
        List<Sample> result = new ArrayList<>();

        for(int i=0; i<samples.getValues().size(); i++) {
            result.add(new Sample(
                            samples.getValues().get(i),
                            samples.getCounts().get(i)
                    ));
        }

        return List.copyOf(result);
    }

    public int allCount() {
        return samples.getCounts().stream().mapToInt(Integer::intValue).sum() + outliersLeft + outliersRight;
    }

    public List<BigDecimal> calculateImprovingProbabilities(double boxSize, int boxesCount) {
        int samplesCount = allCount();
        List<Sample> samples = getSamplesAsList();

        return IntStream.range(0, boxesCount).mapToObj(box -> {
                    double thres = boxSize * box;

                    int samplesInBox = samples.stream()
                            .filter(s -> s.getValue() > thres)
                            .mapToInt(Sample::getCount)
                            .sum();
                    return toRatio(samplesInBox + outliersRight, samplesCount);
                }).collect(Collectors.toList());
    }

    public List<BigDecimal> calculateWorseningProbabilities(double boxSize, int boxesCount) {
        int samplesCount = allCount();
        List<Sample> samples = getSamplesAsList();

        return IntStream.range(0, boxesCount).mapToObj(box -> {
                    double thres = -1 * boxSize * box;

                    int samplesInBox = samples.stream()
                            .filter(s -> s.getValue() < thres)
                            .mapToInt(Sample::getCount)
                            .sum();

                    return toRatio(samplesInBox + outliersLeft, samplesCount);
                }).collect(Collectors.toList());
    }

    private static BigDecimal toRatio(int sampleCount, int allCount) {
        return BigDecimal.valueOf(sampleCount / (double) allCount).setScale(4, RoundingMode.HALF_DOWN);
    }

    public int getOutliersLeft() {
        return outliersLeft;
    }

    public int getOutliersRight() {
        return outliersRight;
    }

    static class Sample {
        private final double value;
        private final int count;

        public Sample(double value, int count) {
            this.value = value;
            this.count = count;
        }

        public double getValue() {
            return value;
        }

        public int getCount() {
            return count;
        }
    }
}
