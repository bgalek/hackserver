package pl.allegro.tech.leaders.hackathon.registration.api;

import java.net.InetSocketAddress;

public class TeamUpdate {
    private final String name;
    private final InetSocketAddress remoteAddress;
    private final TeamSecret secret;

    public TeamUpdate(String name, InetSocketAddress remoteAddress, TeamSecret secret) {
        this.name = name;
        this.remoteAddress = remoteAddress;
        this.secret = secret;
    }

    public String getName() {
        return name;
    }

    public InetSocketAddress getRemoteAddress() {
        return remoteAddress;
    }

    public TeamSecret getSecret() {
        return secret;
    }
}
