package pl.allegro.tech.leaders.hackathon.registration.infrastructure.web;

class RegisteredTeamResponse {

    private final String name;
    private final String address;

    RegisteredTeamResponse(String name, String address) {
        this.name = name;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getAvatar() {
        return String.format("https://api.adorable.io/avatars/285/%s%%40%s", name, address);
    }
}
