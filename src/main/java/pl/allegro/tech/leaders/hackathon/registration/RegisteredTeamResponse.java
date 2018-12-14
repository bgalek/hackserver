package pl.allegro.tech.leaders.hackathon.registration;

/**
 * Registered Team Api Entity
 */
class RegisteredTeamResponse {

    private final String name;

    RegisteredTeamResponse(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
