package pl.allegro.tech.leaders.hackathon.registration.api;

import java.net.InetSocketAddress;

public class RegisteredTeam {
    private final String name;
    private final InetSocketAddress remoteAddress;
    private final String secret;
    private HealthStatus health;

    public RegisteredTeam(String name, InetSocketAddress remoteAddress, String secret) {
        this(name, remoteAddress, secret, HealthStatus.UNKNOWN);
    }

    public RegisteredTeam(String name, InetSocketAddress remoteAddress, String secret, HealthStatus healthStatus) {
        this.name = name;
        this.remoteAddress = remoteAddress;
        this.secret = secret;
        this.health = healthStatus;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return name;
    }

    public InetSocketAddress getRemoteAddress() {
        return remoteAddress;
    }

    public String getSecret() {
        return secret;
    }

    public void setHealth(HealthStatus health) {
        this.health = health;
    }

    public HealthStatus getHealth() {
        return health;
    }
}
