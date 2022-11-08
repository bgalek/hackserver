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
    private final String remoteAddress;
    private final String secret;

    @PersistenceConstructor
    private Team(String id, String name, String remoteAddress, String secret) {
        this.id = id;
        this.name = name;
        this.remoteAddress = remoteAddress;
        this.secret = secret;
    }

    Team(String name, String remoteAddress) {
        this(null, name, remoteAddress, UUID.randomUUID().toString());
    }

    Team(Team team, String remoteAddress) {
        this(team.getId(), team.getName(), remoteAddress, team.getSecret());
    }

    public String getId() {
        return id;
    }

    String getName() {
        return name;
    }

    String getRemoteAddress() {
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
