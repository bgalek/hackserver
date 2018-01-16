package pl.allegro.experiments.chi.chiserver.domain.experiments


/**
 * Represents range of numbers, right-open.
 * Left bound is inclusive, right bound is NOT inclusive.
 */
abstract class Range<out T : Number>(val from: T, val to: T) {

    init {
        require(from.toDouble() >= 0) { "Range.from < 0 in Range($from..$to)" }
        require(from.toDouble() <= to.toDouble()) { "Range.from > Range.to in Range($from..$to)" }
    }
}

class PercentageRange(from: Int, to: Int) : Range<Int>(from, to) {
    init {
        require(to <= 100) { "Range.to > 100 in PercentageRange($from..$to)" }
    }
}
