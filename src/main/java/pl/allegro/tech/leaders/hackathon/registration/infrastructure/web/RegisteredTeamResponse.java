package pl.allegro.tech.leaders.hackathon.registration.infrastructure.web;

class RegisteredTeamResponse {

    private final String name;
    private final String address;
    private final int port;
    private final boolean health;

    RegisteredTeamResponse(String name, String address, int port, boolean health) {
        this.name = name;
        this.address = address;
        this.port = port;
        this.health = health;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    public String getAvatar() {
        return String.format("https://avatars.dicebear.com/api/male/%s%%40%s.svg", name, address);
    }

    public boolean getHealth() {
        return health;
    }
}
