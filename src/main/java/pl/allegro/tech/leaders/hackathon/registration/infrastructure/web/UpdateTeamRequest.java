package pl.allegro.tech.leaders.hackathon.registration.infrastructure.web;

import com.fasterxml.jackson.annotation.JsonProperty;

class UpdateTeamRequest {

    private final int port;

    UpdateTeamRequest(@JsonProperty("port") int port) {
        this.port = port;
    }

    int getPort() {
        return port;
    }
}
