package pl.allegro.tech.leaders.hackathon.registration;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.net.InetAddress;
import java.util.UUID;

@Document
class Team {
    @Id
    private String id;
    @Indexed(name = "name", unique = true)
    private final String name;
    private final InetAddress remoteAddress;
    private final String secret;

    Team(String name, InetAddress remoteAddress) {
        this(name, remoteAddress, UUID.randomUUID().toString());
    }

    @PersistenceConstructor
    private Team(String name, InetAddress remoteAddress, String secret) {
        this.name = name;
        this.remoteAddress = remoteAddress;
        this.secret = secret;
    }

    String getName() {
        return name;
    }

    InetAddress getRemoteAddress() {
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
