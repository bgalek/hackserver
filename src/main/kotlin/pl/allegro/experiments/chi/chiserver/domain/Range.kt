package pl.allegro.experiments.chi.chiserver.domain


import com.google.common.base.Preconditions

/**
 * Represents range of numbers, right-open.
 * Left bound is inclusive, right bound is NOT inclusive.
 */
abstract class Range<T : Number> internal constructor(val from: T, val to: T) {

    init {
        Preconditions.checkArgument(from.toDouble() >= 0, "Range.from < 0 in Range($from..$to)")
        Preconditions.checkArgument(from.toDouble() < to.toDouble(), "Range.from >= Range.to in Range($from..$to)")
    }

    internal abstract operator fun contains(value: T): Boolean
}
