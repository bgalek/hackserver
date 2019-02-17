package pl.allegro.tech.leaders.hackathon.registration;

import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.NOT_FOUND;

class TeamNotFoundException extends ResponseStatusException {
    TeamNotFoundException(String teamId) {
        super(NOT_FOUND, "Team '"+teamId+"' not found.");
    }
}
