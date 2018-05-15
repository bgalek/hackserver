package pl.allegro.experiments.chi.chiserver.domain.statistics.bayes;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.List;

public class VariantBayesianStatistics {
    private final String variantName;
    private final Samples samples;
    private int outliersLeft;
    private int outliersRight;

    public VariantBayesianStatistics(String variantName,
                                     Samples samples,
                                     int outliersLeft,
                                     int outliersRight) {
        this.variantName = variantName;
        this.samples = samples;
        this.outliersLeft = outliersLeft;
        this.outliersRight = outliersRight;
    }

    public String getVariantName() {
        return variantName;
    }

    public Samples getSamples() {
        return samples;
    }

    @JsonIgnore
    public List<Sample> getSamplesAsList() {
        List<Sample> result = new ArrayList<>();

        for(int i=0; i<samples.getValues().size(); i++) {
            result.add(new Sample(
                            samples.getValues().get(i),
                            samples.getCounts().get(i),
                            samples.getLabels().get(i)
                    ));
        }

        return List.copyOf(result);
    }

    public int allCount() {
        return samples.getCounts().stream().mapToInt(Integer::intValue).sum() + outliersLeft + outliersRight;
    }

    public int getOutliersLeft() {
        return outliersLeft;
    }

    public int getOutliersRight() {
        return outliersRight;
    }

    public static class Sample {
        private final double value;
        private final int count;
        private final String label;

        public Sample(double value, int count, String label) {
            this.value = value;
            this.count = count;
            this.label = label;
        }

        public double getValue() {
            return value;
        }

        public int getCount() {
            return count;
        }

        public String getLabel() {
            return label;
        }
    }
}
