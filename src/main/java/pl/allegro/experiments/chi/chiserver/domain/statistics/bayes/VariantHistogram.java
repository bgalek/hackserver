package pl.allegro.experiments.chi.chiserver.domain.statistics.bayes;

import com.google.common.base.Preconditions;

import java.math.BigDecimal;
import java.util.List;

public class VariantHistogram {
    private final String variantName;

    private final List<Double> values;
    private final List<BigDecimal> frequencies;
    private final List<BigDecimal> labels;

    public VariantHistogram(String variantName, List<Double> values, List<BigDecimal> frequencies, List<BigDecimal> labels) {
        Preconditions.checkNotNull(variantName);
        Preconditions.checkNotNull(values);
        Preconditions.checkNotNull(frequencies);
        Preconditions.checkNotNull(labels);
        Preconditions.checkArgument(values.size() == frequencies.size(), "values and frequencies should be the same length");
        Preconditions.checkArgument(values.size() == labels.size(), "values and labels should be the same length");

        this.variantName = variantName;
        this.values = values;
        this.frequencies = frequencies;
        this.labels = labels;
    }

    public String getVariantName() {
        return variantName;
    }

    public List<Double> getValues() {
        return values;
    }

    public List<BigDecimal> getFrequencies() {
        return frequencies;
    }

    public List<BigDecimal> getLabels() {
        return labels;
    }
}
