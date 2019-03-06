package pl.allegro.tech.leaders.hackathon.registration.api;

import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.NOT_FOUND;

public class TeamNotFoundException extends ResponseStatusException {
    public TeamNotFoundException(String teamId) {
        super(NOT_FOUND, String.format("Team '%s' not found.", teamId));
    }
}
