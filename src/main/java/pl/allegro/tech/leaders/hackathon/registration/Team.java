package pl.allegro.tech.leaders.hackathon.registration;

import org.springframework.data.annotation.Id;

public class Team {
    @Id
    private final String name;
    private final String remoteAddr;

    public String getName() {
        return name;
    }

    public String getRemoteAddr() {
        return remoteAddr;
    }

    public String getHttpRemoteAddr() {
        //TODO we don't want to hardcode the port?
        return "http://" + remoteAddr + ":8080";
    }

    Team(String name, String remoteAddr) {
        this.name = name;
        this.remoteAddr = remoteAddr;
    }

    @Override
    public String toString() {
        return "Team{" +
                "name='" + name + '\'' +
                ", remoteAddr='" + remoteAddr + '\'' +
                '}';
    }
}
