package pl.allegro.tech.leaders.hackathon.registration;

class Team {
    private final String name;

    String getName() {
        return name;
    }

    String getRemoteAddr() {
        return remoteAddr;
    }

    private final String remoteAddr;

    Team(String name, String remoteAddr) {

        this.name = name;
        this.remoteAddr = remoteAddr;
    }
}
