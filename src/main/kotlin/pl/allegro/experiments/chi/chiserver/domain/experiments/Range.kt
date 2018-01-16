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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Range<*>

        if (from != other.from) return false
        if (to != other.to) return false

        return true
    }

    override fun hashCode(): Int {
        var result = from.hashCode()
        result = 31 * result + to.hashCode()
        return result
    }
}

class PercentageRange(from: Int, to: Int) : Range<Int>(from, to) {
    init {
        require(to <= 100) { "Range.to > 100 in PercentageRange($from..$to)" }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false
        return true
    }
}
