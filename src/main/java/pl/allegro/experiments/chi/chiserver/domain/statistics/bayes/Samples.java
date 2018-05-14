package pl.allegro.experiments.chi.chiserver.domain.statistics.bayes;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;
import org.javers.core.diff.changetype.container.ListChange;

import java.util.Arrays;
import java.util.List;

public class Samples {
    private List<Double> values;
    private List<Integer> counts;
    private List<String> labels;

    @JsonCreator
    public Samples(@JsonProperty("values") List<Double> values,
                   @JsonProperty("counts") List<Integer> counts,
                   @JsonProperty("labels") List<String> labels) {
        Preconditions.checkNotNull(values, "values should not be null");
        Preconditions.checkNotNull(counts, "counts should not be null");
        Preconditions.checkNotNull(labels, "labels should not be null");
        Preconditions.checkArgument(values.size() == counts.size(), "values and counts should be the same length");
        Preconditions.checkArgument(labels.size() == counts.size(), "labels and counts should be the same length");
        this.values = List.copyOf(values);
        this.counts = List.copyOf(counts);
        this.labels = List.copyOf(labels);
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
