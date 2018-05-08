package pl.allegro.experiments.chi.chiserver.domain.statistics.bayes;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;

public class Samples {
    private double[] values;
    private int[] counts;

    @JsonCreator
    public Samples(@JsonProperty("values") double[] values, @JsonProperty("counts") int[] counts) {
        Preconditions.checkNotNull(values, "values should not be null");
        Preconditions.checkNotNull(counts, "counts should not be null");
        Preconditions.checkArgument(values.length == counts.length, "values and counts should be the same length");
        this.values = values;
        this.counts = counts;
    }

    public double[] getValues() {
        return values;
    }

    public int[] getCounts() {
        return counts;
    }
}
