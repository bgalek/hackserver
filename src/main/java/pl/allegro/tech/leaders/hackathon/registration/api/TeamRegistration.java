package pl.allegro.tech.leaders.hackathon.registration.api;

import java.net.InetAddress;

public class TeamRegistration {
    private final String name;
    private final InetAddress remoteAddress;

    public TeamRegistration(String name, InetAddress remoteAddress) {
        this.name = name;
        this.remoteAddress = remoteAddress;
    }

    public String getName() {
        return name;
    }

    public InetAddress getRemoteAddress() {
        return remoteAddress;
    }
}
