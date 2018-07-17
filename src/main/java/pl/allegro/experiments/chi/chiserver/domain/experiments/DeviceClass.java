package pl.allegro.experiments.chi.chiserver.domain.experiments;

public enum DeviceClass {
    phone,
    phone_iphone,
    phone_android,
    desktop,
    tablet,
    all;

    public static DeviceClass fromString(String deviceClass) {
        if (deviceClass == null || deviceClass.isEmpty()) {
            return all;
        } else if ("smartphone".equals(deviceClass)) {
            return phone;
        }
        return valueOf(deviceClass.replace('-', '_').toLowerCase());
    }

    public String toJsonString() {
        if (this == all) {
            return null;
        }
        return name().replace('_', '-');
    }
}
