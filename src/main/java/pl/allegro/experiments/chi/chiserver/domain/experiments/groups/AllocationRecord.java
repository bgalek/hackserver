package pl.allegro.experiments.chi.chiserver.domain.experiments.groups;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.annotation.PersistenceConstructor;
import pl.allegro.experiments.chi.chiserver.domain.experiments.PercentageRange;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static pl.allegro.experiments.chi.chiserver.domain.experiments.groups.AllocationTable.SHARED_BASE;

final class AllocationRecord {
    private final String experimentId;
    private final String variant;
    private final PercentageRange range;

    @PersistenceConstructor
    AllocationRecord(String experimentId, String variant, PercentageRange range) {
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
        return new AllocationRecord(SHARED_BASE, AllocationTable.BASE, range);
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

    boolean isSharedBase() {
        return isBase() && SHARED_BASE.equals(experimentId);
    }

    int getFrom() {
        return range.getFrom();
    }

    int getTo() {
        return range.getTo();
    }

    int size() {
        return range.size();
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
        return (this.getExperimentId().equals(SHARED_BASE) && right.getExperimentId().equals(SHARED_BASE)) ||
                (this.getTo() == right.getFrom() &&
                    this.getExperimentId().equals(right.getExperimentId()) &&
                    this.getVariant().equals(right.getVariant()));
    }

    boolean belongsTo(String experimentId) {
        return this.experimentId.equals(experimentId);
    }

    boolean belongsTo(String experimentId, String variant) {
        return this.experimentId.equals(experimentId) &&
               this.variant.equals(variant);
    }

    boolean belongsToOrShared(String experimentId, String variant) {
        return this.variant.equals(variant) &&
              (this.experimentId.equals(experimentId) || isSharedBase());
    }

    AllocationRecord joined(AllocationRecord right) {
        return new AllocationRecord(
               this.getExperimentId(),
               this.getVariant(),
               new PercentageRange(this.getFrom(), right.getTo()));
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