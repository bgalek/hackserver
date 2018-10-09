package pl.allegro.experiments.chi.chiserver.domain.experiments;

import com.google.common.base.Preconditions;

public final class PercentageRange extends Range {
    public PercentageRange(int from, int to) {
        super(from, to);
        Preconditions.checkArgument(to <= 100, "Range.to > 100");
    }

    public int size() {
        return getTo() - getFrom();
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    public PercentageRange trim(int newSize, boolean fromRight) {
        Preconditions.checkArgument(newSize <= size());

        if (fromRight) {
            return new PercentageRange(getTo() - newSize, getTo());
        } else {
            return new PercentageRange(getFrom(), getFrom() + newSize);
        }
    }
}