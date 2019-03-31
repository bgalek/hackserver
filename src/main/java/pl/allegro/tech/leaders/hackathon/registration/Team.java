package pl.allegro.tech.leaders.hackathon.registration;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.net.InetSocketAddress;
import java.util.UUID;

@Document
class Team {
    @Id
    private String id;
    @Indexed(name = "name", unique = true)
    private final String name;
    private final InetSocketAddress remoteAddress;
    private final String secret;

    @PersistenceConstructor
    private Team(String name, InetSocketAddress remoteAddress, String secret) {
        this.name = name;
        this.remoteAddress = remoteAddress;
        this.secret = secret;
    }

    Team(String name, InetSocketAddress remoteAddress) {
        this(name, remoteAddress, UUID.randomUUID().toString());
    }

    Team(Team team, InetSocketAddress remoteAddress) {
        this(team.getName(), remoteAddress, team.getSecret());
    }

    public String getId() {
        return id;
    }

    String getName() {
        return name;
    }

    InetSocketAddress getRemoteAddress() {
        return remoteAddress;
    }

    String getSecret() {
        return secret;
    }

    @Override
    public String toString() {
        return "Team{" +
                "name='" + name + '\'' +
                ", remoteAddr='" + remoteAddress + '\'' +
                '}';
    }
}
