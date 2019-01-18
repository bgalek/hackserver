package pl.allegro.tech.leaders.hackathon.challenge.api;

import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.NOT_FOUND;

public class ChallengeNotFoundException extends ResponseStatusException {
    public ChallengeNotFoundException(String challengeId) {
        super(NOT_FOUND, "Challenge not found. Id: " + challengeId);
    }
}
