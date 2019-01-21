package pl.allegro.tech.leaders.hackathon.runner;

import org.springframework.http.ResponseEntity;

public class FailedResult extends TaskResult {
    private final String errorMessage;

    FailedResult(ResponseEntity<String> response, String errorMessage) {
        super(response, 0);
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
