package pl.allegro.experiments.chi.chiserver.domain.statistics.bayes;

import com.google.common.base.Preconditions;

import java.util.List;

public class VariantHistogram {
    private final String variantName;

    private final List<Double> values;
    private final List<Integer> counts;
    private final List<String> labels;

    public VariantHistogram(String variantName, List<Double> values, List<Integer> counts, List<String> labels) {
        Preconditions.checkNotNull(variantName);
        Preconditions.checkNotNull(values);
        Preconditions.checkNotNull(counts);
        Preconditions.checkNotNull(labels);
        Preconditions.checkArgument(values.size() == counts.size(), "values and counts should be the same length");
        Preconditions.checkArgument(values.size() == labels.size(), "values and labels should be the same length");

        this.variantName = variantName;
        this.values = values;
        this.counts = counts;
        this.labels = labels;
    }

    public String getVariantName() {
        return variantName;
    }

    public List<Double> getValues() {
        return values;
    }

    public List<Integer> getCounts() {
        return counts;
    }

    public List<String> getLabels() {
        return labels;
    }
}
