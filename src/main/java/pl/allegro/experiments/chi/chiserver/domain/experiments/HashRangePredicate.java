package pl.allegro.experiments.chi.chiserver.domain.experiments;

import com.google.common.base.Preconditions;

public class HashRangePredicate implements Predicate {

    private final Range hashRange;

    public HashRangePredicate(Range hashRange) {
        Preconditions.checkNotNull(hashRange);
        this.hashRange = hashRange;
    }

    public Range getHashRange() {
        return hashRange;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HashRangePredicate that = (HashRangePredicate) o;

        return hashRange.equals(that.hashRange);
    }

    @Override
    public int hashCode() {
        return hashRange.hashCode();
    }
}