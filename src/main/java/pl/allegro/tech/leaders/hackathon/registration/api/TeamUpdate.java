package pl.allegro.tech.leaders.hackathon.registration.api;

import java.net.InetAddress;

public class TeamUpdate {
    private final String name;
    private final InetAddress remoteAddress;
    private final TeamSecret secret;

    public TeamUpdate(String name, InetAddress remoteAddress, TeamSecret secret) {
        this.name = name;
        this.remoteAddress = remoteAddress;
        this.secret = secret;
    }

    public String getName() {
        return name;
    }

    public InetAddress getRemoteAddress() {
        return remoteAddress;
    }

    public TeamSecret getSecret() {
        return secret;
    }
}
