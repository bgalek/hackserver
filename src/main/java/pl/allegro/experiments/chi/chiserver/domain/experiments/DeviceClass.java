package pl.allegro.experiments.chi.chiserver.domain.experiments;

public enum DeviceClass {
    phone,
    desktop,
    tablet,
    all;

    public static DeviceClass fromString(String deviceClass) {
        if (deviceClass == null || deviceClass.isEmpty()) {
            return all;
        } else if ("smartphone".equals(deviceClass)) {
            return phone;
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
