package pl.allegro.tech.leaders.hackathon.registration.api;

import java.net.InetSocketAddress;

public class TeamRegistration {
    private final String name;
    private final String remoteAddress;

    public TeamRegistration(String name, String remoteAddress) {
        this.name = name;
        this.remoteAddress = remoteAddress;
    }

    public String getName() {
        return name;
    }

    public String getRemoteAddress() {
        return remoteAddress;
    }
}
