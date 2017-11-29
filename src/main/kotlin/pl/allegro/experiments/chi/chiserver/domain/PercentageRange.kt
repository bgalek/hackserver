package pl.allegro.experiments.chi.chiserver.domain

import com.google.common.base.Preconditions

class PercentageRange internal constructor(from: Int, to: Int) : Range<Int>(from, to) {
    init {
        Preconditions.checkArgument(to <= 100, "Range.to > 100 in PercentageRange($from..$to)")
    }

    override internal operator fun contains(value: Int): Boolean {
        return value >= from && value < to
    }
}
