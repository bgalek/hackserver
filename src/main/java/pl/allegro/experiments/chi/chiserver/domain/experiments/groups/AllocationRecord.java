package pl.allegro.experiments.chi.chiserver.domain.experiments.groups;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.annotation.PersistenceConstructor;
import pl.allegro.experiments.chi.chiserver.domain.experiments.PercentageRange;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class AllocationRecord {
    private final String experimentId;
    private final String variant;
    private final PercentageRange range;

    @PersistenceConstructor
    public AllocationRecord(String experimentId, String variant, PercentageRange range) {
        Preconditions.checkArgument(StringUtils.isNotBlank(experimentId));
        Preconditions.checkArgument(StringUtils.isNotBlank(variant));
        Preconditions.checkArgument(range != null);

        this.experimentId = experimentId;
        this.variant = variant;
        this.range = range;
    }

    static AllocationRecord forVariant(String experimentId, String variant, int from, int to) {
        return AllocationRecord.forVariant(experimentId, variant, new PercentageRange(from, to));
    }

    static AllocationRecord forVariant(String experimentId, String variant, PercentageRange range) {
        return new AllocationRecord(experimentId, variant, range);
    }

    static AllocationRecord forSharedBase(int from, int to) {
        return forSharedBase(new PercentageRange(from, to));
    }

    static AllocationRecord forSharedBase(PercentageRange range) {
        return new AllocationRecord(AllocationTable.SHARED_BASE, "base", range);
    }

    List<Integer> getBuckets() {
        var result = new ArrayList<Integer>();
        for (int i = range.getFrom(); i < range.getTo(); i++) {
            result.add(i);
        }
        return ImmutableList.copyOf(result);
    }

    boolean isBase() {
        return AllocationTable.BASE.equals(variant);
    }


    public boolean isSharedBase() {
        return isBase() && AllocationTable.SHARED_BASE.equals(experimentId);
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

    boolean canJoinWith(AllocationRecord right) {
        return this.getRange().getTo() == right.getRange().getFrom() &&
               this.getExperimentId().equals(right.getExperimentId()) &&
               this.getVariant().equals(right.getVariant());
    }

    boolean belongsTo(String experimentId) {
        return this.experimentId.equals(experimentId);
    }

    AllocationRecord joined(AllocationRecord right) {
        return new AllocationRecord(
               this.getExperimentId(),
               this.getVariant(),
               new PercentageRange(this.getRange().getFrom(), right.getRange().getTo()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AllocationRecord that = (AllocationRecord) o;
        return  Objects.equals(experimentId, that.experimentId) &&
                Objects.equals(variant, that.variant) &&
                Objects.equals(range, that.range);
    }

    @Override
    public int hashCode() {
        return Objects.hash(experimentId, variant, range);
    }

    @Override
    public String toString() {
        return "AllocationRecord{" +
                "experimentId='" + experimentId + '\'' +
                ", variant='" + variant + '\'' +
                ", range=" + range +
                '}';
    }
}
