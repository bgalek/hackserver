package pl.allegro.experiments.chi.chiserver.domain.statistics.bayes;

import com.google.common.base.Preconditions;

import java.util.List;

public class Samples {
    private List<Double> values;
    private List<Integer> counts;

    public Samples(List<Double> values,
                   List<Integer> counts) {
        Preconditions.checkNotNull(values, "values should not be null");
        Preconditions.checkNotNull(counts, "counts should not be null");
        Preconditions.checkArgument(values.size() == counts.size(), "values and counts should be the same length");
        this.values = List.copyOf(values);
        this.counts = List.copyOf(counts);
    }

    public List<Double> getValues() {
        return values;
    }

    public List<Integer> getCounts() {
        return counts;
    }
}
