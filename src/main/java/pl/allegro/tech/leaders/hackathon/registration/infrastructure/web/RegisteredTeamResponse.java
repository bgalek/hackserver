package pl.allegro.tech.leaders.hackathon.registration.infrastructure.web;

class RegisteredTeamResponse {

    private final String name;
    private final String address;
    private final int port;

    RegisteredTeamResponse(String name, String address, int port) {
        this.name = name;
        this.address = address;
        this.port = port;
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
        return String.format("https://api.adorable.io/avatars/285/%s%%40%s", name, address);
    }
}
