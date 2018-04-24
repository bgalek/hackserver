package pl.allegro.experiments.chi.chiserver.domain.statistics.bayes;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;

import java.math.BigDecimal;
import java.util.List;

public class Samples {
    private double[] values;
    private int[] counts;

    public Samples(List<BigDecimal> values, List<Integer> counts) {
        this( values.stream().map(BigDecimal::doubleValue).mapToDouble(Double::doubleValue).toArray(), counts.stream().mapToInt(Integer::intValue).toArray());
    }

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
