package pl.allegro.experiments.chi.chiserver.domain.experiments

import java.util.regex.Pattern

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