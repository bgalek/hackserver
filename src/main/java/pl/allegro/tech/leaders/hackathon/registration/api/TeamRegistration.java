package pl.allegro.tech.leaders.hackathon.registration.api;

import java.net.InetSocketAddress;

public class TeamRegistration {
    private final String name;
    private final InetSocketAddress remoteAddress;

    public TeamRegistration(String name, InetSocketAddress remoteAddress) {
        this.name = name;
        this.remoteAddress = remoteAddress;
    }

    public String getName() {
        return name;
    }

    public InetSocketAddress getRemoteAddress() {
        return remoteAddress;
    }
}
