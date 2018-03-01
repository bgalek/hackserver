package pl.allegro.experiments.chi.chiserver.domain.experiments;

import com.google.common.base.Preconditions;

public class PercentageRange extends Range {
    public PercentageRange(int from, int to) {
        super(from, to);
        Preconditions.checkArgument(to <= 100, "Range.to > 100");
    }
}