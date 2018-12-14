package pl.allegro.tech.leaders.hackathon.registration;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Registered Team Api Entity
 */
class RegisterTeamRequest {

    private final String name;

    RegisterTeamRequest(@JsonProperty("name") String name) {
        this.name = name;
    }

    String getName() {
        return name;
    }

}
