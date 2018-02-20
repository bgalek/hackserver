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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DeviceClassPredicate that = (DeviceClassPredicate) o;

        return device.equals(that.device);
    }

    @Override
    public int hashCode() {
        return device.hashCode();
    }
}