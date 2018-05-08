package pl.allegro.experiments.chi.chiserver.domain.experiments;

import java.util.Optional;

public enum DeviceClass {
    phone,
    desktop,
    tablet,
    all;

    public static DeviceClass fromString(String deviceClass) {
        if (deviceClass == null || deviceClass.isEmpty()) {
            return all;
        }
        return valueOf(deviceClass.toLowerCase());
    }

    public String toJsonString() {
        if (this == all) {
            return null;
        }
        return name();
    }
}
