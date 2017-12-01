package pl.allegro.experiments.chi.chiserver.domain

import com.google.common.base.Preconditions

class PercentageRange(from: Int, to: Int) : Range<Int>(from, to) {
    init {
        Preconditions.checkArgument(to <= 100, "Range.to > 100 in PercentageRange($from..$to)")
    }

    override operator fun contains(value: Int): Boolean {
        return value in from..(to - 1)
    }
}
