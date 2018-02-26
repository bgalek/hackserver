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

}