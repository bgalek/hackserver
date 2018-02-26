package pl.allegro.experiments.chi.chiserver.domain.experiments;

import com.google.common.base.Preconditions;

abstract public class Range {
    private final int from;
    private final int to;

    public Range(int from, int to) {
        Preconditions.checkArgument(from >= 0, "Range.from < 0");
        Preconditions.checkArgument(from <= to, "Range.from > Range.to");
        this.from = from;
        this.to = to;
    }

    public int getFrom() {
        return from;
    }

    public int getTo() {
        return to;
    }
}
