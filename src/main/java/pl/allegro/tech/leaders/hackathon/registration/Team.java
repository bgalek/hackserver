package pl.allegro.tech.leaders.hackathon.registration;

import java.net.InetSocketAddress;

class Team {
    private final String name;
    private final InetSocketAddress remoteAddress;

    Team(String name, InetSocketAddress remoteAddress) {
        this.name = name;
        this.remoteAddress = remoteAddress;
    }

    String getName() {
        return name;
    }

    InetSocketAddress getRemoteAddress() {
        return remoteAddress;
    }

    @Override
    public String toString() {
        return "Team{" +
                "name='" + name + '\'' +
                ", remoteAddr='" + remoteAddress + '\'' +
                '}';
    }
}
