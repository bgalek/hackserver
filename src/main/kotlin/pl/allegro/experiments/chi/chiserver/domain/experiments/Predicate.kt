package pl.allegro.experiments.chi.chiserver.domain.experiments

import java.util.regex.Pattern

interface Predicate

class HashRangePredicate(val hashRange: Range) : Predicate {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as HashRangePredicate

        if (hashRange != other.hashRange) return false

        return true
    }

    override fun hashCode(): Int {
        return hashRange.hashCode()
    }
}

class DeviceClassPredicate(device: String) : Predicate {
    val device: String

    init {
        require(device.isNotBlank()) { "empty device in DeviceClassPredicate" }

        this.device = device.toLowerCase()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DeviceClassPredicate

        if (device != other.device) return false

        return true
    }

    override fun hashCode(): Int {
        return device.hashCode()
    }
}

class CmuidRegexpPredicate(val pattern: Pattern) : Predicate {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CmuidRegexpPredicate

        if (pattern != other.pattern) return false

        return true
    }

    override fun hashCode(): Int {
        return pattern.hashCode()
    }
}

class InternalPredicate : Predicate {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        return true
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}
