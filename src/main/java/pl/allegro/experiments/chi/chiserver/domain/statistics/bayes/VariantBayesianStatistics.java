package pl.allegro.experiments.chi.chiserver.domain.statistics.bayes;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.lang.Math.min;

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

    int radius() {
        return getSamples().getValues().size() / 2;
    }

    VariantBayesianStatistics noiseReduction() {
        int samplesCount = samples.getValues().size();
        Preconditions.checkState(samplesCount % 2 == 0);

        var newValues = new ArrayList<Double>();
        var newCounts = new ArrayList<Integer>();

        for (int p=0; p<samplesCount/2; p++) {
            int i = p * 2;
            int j = p * 2 + 1;

            var leftValue = samples.getValues().get(i);
            var rightValue = samples.getValues().get(j);
            var newValue = rightValue >= 0 ? rightValue : leftValue;
            newValues.add(newValue);

            var newCount = samples.getCounts().get(i) + samples.getCounts().get(j);
            newCounts.add(newCount);
        }

        return new VariantBayesianStatistics(variantName, new Samples(newValues, newCounts), outliersLeft, outliersRight);
    }

    public String getVariantName() {
        return variantName;
    }

    public Samples getSamples() {
        return samples;
    }

    private List<Sample> getSamplesAsList() {
        return IntStream.range(0, min(samples.getValues().size(), samples.getCounts().size()))
                .mapToObj(i -> new Sample(samples.getValues().get(i), samples.getCounts().get(i)))
                .collect(Collectors.toList());
    }


    public int allCount() {
        return samples.getCounts().stream().mapToInt(Integer::intValue).sum() + outliersLeft + outliersRight;
    }

    public List<BigDecimal> calculateImprovingProbabilities(double boxSize, int boxesCount) {
        int samplesCount = allCount();
        List<Sample> samples = getSamplesAsList();
        List<BigDecimal> result = new ArrayList<>();
        int sum = outliersRight;
        int box = boxesCount - 1;
        int index = samples.size() - 1;
        while (box >= 0) {
            double thres = boxSize * box;
            while (samples.get(index).getValue() > thres) {
                sum += samples.get(index).getCount();
                index --;
            }
            result.add(toRatio(sum, samplesCount));
            box --;
        }
        return Lists.reverse(result);
    }

    public List<BigDecimal> calculateWorseningProbabilities(double boxSize, int boxesCount) {
        int samplesCount = allCount();
        List<Sample> samples = getSamplesAsList();
        List<BigDecimal> result = new ArrayList<>();
        int sum = outliersLeft;
        int box = boxesCount - 1;
        int index = 0;
        while (box >= 0) {
            double thres = -1 * boxSize * box;
            while (samples.get(index).getValue() < thres) {
                sum += samples.get(index).getCount();
                index ++;
            }
            result.add(toRatio(sum, samplesCount));
            box --;
        }
        return Lists.reverse(result);
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

    public List<BigDecimal> calculateFrequencies() {
        var allCount = allCount();
        return samples.getCounts().stream()
                .map(it -> toRatio(it,allCount))
                .collect(Collectors.toList());
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
