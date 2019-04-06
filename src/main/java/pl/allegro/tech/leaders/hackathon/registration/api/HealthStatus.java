package pl.allegro.tech.leaders.hackathon.registration.api;

public enum HealthStatus {
    UNKNOWN(false), HEALTHY(true), DEAD(false);

    private final boolean isWorking;

    HealthStatus(boolean isWorking) {
        this.isWorking = isWorking;
    }

    public boolean isWorking() {
        return isWorking;
    }
}
