package pl.allegro.experiments.chi.chiserver.domain.experiments;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.Nullable;

public class PercentageRange extends Range<Integer> {
    public PercentageRange(int from, int to) {
        super(from, to);
        Preconditions.checkArgument(to <= 100, "Range.to > 100");
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return super.equals(o);
    }
}