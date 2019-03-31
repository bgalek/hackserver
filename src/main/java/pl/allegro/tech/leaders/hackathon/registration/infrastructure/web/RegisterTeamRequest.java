package pl.allegro.tech.leaders.hackathon.registration.infrastructure.web;

import com.fasterxml.jackson.annotation.JsonProperty;

class RegisterTeamRequest {

    private final String name;
    private final int port;

    RegisterTeamRequest(@JsonProperty(value = "name", required = true) String name,
                        @JsonProperty(value = "port", required = true) int port) {
        this.name = name;
        this.port = port;
    }

    String getName() {
        return name;
    }

    int getPort() {
        return port;
    }
}
