package pl.allegro.experiments.chi.chiserver.domain.statistics.bayes;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;

public class Samples {
    private double[] values;
    private int[] counts;
    private String[] labels;

    @JsonCreator
    public Samples(@JsonProperty("values") double[] values,
                   @JsonProperty("counts") int[] counts,
                   @JsonProperty("labels") String[] labels) {
        Preconditions.checkNotNull(values, "values should not be null");
        Preconditions.checkNotNull(counts, "counts should not be null");
        Preconditions.checkNotNull(labels, "labels should not be null");
        Preconditions.checkArgument(values.length == counts.length, "values and counts should be the same length");
        Preconditions.checkArgument(labels.length == counts.length, "labels and counts should be the same length");
        this.values = values;
        this.counts = counts;
        this.labels = labels;
    }

    public double[] getValues() {
        return values;
    }

    public int[] getCounts() {
        return counts;
    }

    public String[] getLabels() { return labels; }
}
