package pl.allegro.experiments.chi.chiserver.domain.experiments.groups;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import org.springframework.data.annotation.PersistenceConstructor;
import pl.allegro.experiments.chi.chiserver.domain.experiments.PercentageRange;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AllocationRecord {
    private final String experimentId;
    private final String variant;
    private final PercentageRange range;

    @PersistenceConstructor
    public AllocationRecord(String experimentId, String variant, PercentageRange range) {
        Preconditions.checkArgument(experimentId != null);
        Preconditions.checkArgument(variant != null);
        Preconditions.checkArgument(range != null);

        this.experimentId = experimentId;
        this.variant = variant;
        this.range = range;
    }

    static AllocationRecord forVariant(String experimentId, String variant, int from, int to) {
        return new AllocationRecord(experimentId, variant, new PercentageRange(from, to));
    }

    static AllocationRecord forBase(int from, int to) {
        return new AllocationRecord("*", "base", new PercentageRange(from, to));
    }

    List<Integer> getBuckets() {
        var result = new ArrayList<Integer>();
        for(int i=range.getFrom(); i<range.getTo(); i++){
            result.add(i);
        }
        return ImmutableList.copyOf(result);
    }

    boolean isBase() {
        return "base".equals(variant);
    }

    int getAllocation() {
        return range.getTo() - range.getFrom();
    }

    String getExperimentId() {
        return experimentId;
    }

    String getVariant() {
        return variant;
    }

    PercentageRange getRange() {
        return range;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AllocationRecord that = (AllocationRecord) o;
        return Objects.equals(experimentId, that.experimentId) &&
                Objects.equals(variant, that.variant) &&
                Objects.equals(range, that.range);
    }

    @Override
    public int hashCode() {
        return Objects.hash(experimentId, variant, range);
    }
}
