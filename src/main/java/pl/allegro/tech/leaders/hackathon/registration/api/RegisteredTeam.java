package pl.allegro.tech.leaders.hackathon.registration.api;

import java.net.InetSocketAddress;

public class RegisteredTeam {
    private final String name;
    private final InetSocketAddress remoteAddress;
    private final String secret;

    public RegisteredTeam(String name, InetSocketAddress remoteAddress, String secret) {
        this.name = name;
        this.remoteAddress = remoteAddress;
        this.secret = secret;
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
}
