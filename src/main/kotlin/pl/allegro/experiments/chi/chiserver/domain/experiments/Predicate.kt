package pl.allegro.experiments.chi.chiserver.domain.experiments

import java.util.regex.Pattern

interface Predicate

class HashRangePredicate(val hashRange : Range<Int>) : Predicate

class DeviceClassPredicate(device: String) : Predicate {
    val device: String

    init {
        require(device.isNotBlank()) { "empty device in DeviceClassPredicate" }

        this.device = device.toLowerCase()
    }
}

class CmuidRegexpPredicate(val pattern: Pattern)  : Predicate

class InternalPredicate : Predicate
