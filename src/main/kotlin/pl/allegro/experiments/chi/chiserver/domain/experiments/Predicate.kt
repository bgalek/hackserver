package pl.allegro.experiments.chi.chiserver.domain.experiments

import com.google.common.base.Preconditions
import com.google.common.base.Strings
import java.util.regex.Pattern

interface Predicate

class HashRangePredicate(val hashRange : Range<Int>) : Predicate

class DeviceClassPredicate(device: String) : Predicate {
    val device: String

    init {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(device), "empty device in DeviceClassPredicate")

        this.device = device.toLowerCase()
    }
}

class CmuidRegexpPredicate(val pattern: Pattern)  : Predicate

class InternalPredicate : Predicate
