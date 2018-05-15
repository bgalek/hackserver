package pl.allegro.experiments.chi.chiserver.domain.experiments;

public enum DeviceClass {
    //TODO remove phone or smartphone
    phone,
    smartphone,
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
