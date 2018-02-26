package pl.allegro.experiments.chi.chiserver.domain.experiments;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

public class DeviceClassPredicate implements Predicate {
    private final String device;

    public DeviceClassPredicate(String device) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(device));
        this.device = device.toLowerCase();
    }

    public String getDevice() {
        return device;
    }
}