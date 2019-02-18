package pl.allegro.tech.leaders.hackathon.registration;

import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.FORBIDDEN;

class BadSecretException extends ResponseStatusException {
    BadSecretException(String teamName) {
        super(FORBIDDEN, String.format("Bad secret provided for '%s' team", teamName));
    }
}
