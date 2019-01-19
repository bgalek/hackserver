package pl.allegro.tech.leaders.hackathon.registration.infrastructure.web;

import com.fasterxml.jackson.annotation.JsonProperty;

class RegisterTeamRequest {

    private final String name;

    RegisterTeamRequest(@JsonProperty("name") String name) {
        this.name = name;
    }

    String getName() {
        return name;
    }

}
