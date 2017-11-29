package pl.allegro.experiments.chi.chiserver.domain

import com.google.common.base.Preconditions
import com.google.common.base.Strings

class DeviceClassPredicate(device: String) : Predicate {
    val device: String

    init {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(device), "empty device in DeviceClassPredicate")

        this.device = device.toLowerCase()
    }
}
