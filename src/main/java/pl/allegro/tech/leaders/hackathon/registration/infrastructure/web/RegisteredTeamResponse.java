package pl.allegro.tech.leaders.hackathon.registration.infrastructure.web;

class RegisteredTeamResponse {

    private final String name;

    RegisteredTeamResponse(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
