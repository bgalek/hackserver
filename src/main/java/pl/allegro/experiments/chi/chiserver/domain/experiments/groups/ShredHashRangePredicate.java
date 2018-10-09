package pl.allegro.experiments.chi.chiserver.domain.experiments.groups;

import com.google.common.base.Preconditions;
import pl.allegro.experiments.chi.chiserver.domain.experiments.PercentageRange;
import pl.allegro.experiments.chi.chiserver.domain.experiments.Predicate;

import java.util.List;

public class ShredHashRangePredicate implements Predicate {
    private final List<PercentageRange> ranges;
    private final String salt;

    public ShredHashRangePredicate(List<PercentageRange> ranges, String salt) {
        Preconditions.checkNotNull(ranges);
        Preconditions.checkNotNull(salt);
        Preconditions.checkArgument(!ranges.isEmpty(), "empty ranges");
        this.ranges = ranges;
        this.salt = salt;
    }

    public List<PercentageRange> getRanges() {
        return ranges;
    }

    public String getSalt() {
        return salt;
    }
}
