package pl.allegro.experiments.chi.chiserver.domain.statistics.bayes;

import com.google.common.collect.Lists;

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

    private List<Sample> getSamplesAsList() {
        return IntStream.range(0, Math.min(samples.getValues().size(), samples.getCounts().size()))
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
