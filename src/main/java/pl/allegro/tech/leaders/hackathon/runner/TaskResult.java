package pl.allegro.tech.leaders.hackathon.runner;

import org.springframework.http.ResponseEntity;

public class TaskResult {
    private final int score;
    private final String responseBody;
    private final int responseHttpStatus;

    TaskResult(ResponseEntity<String> response, int score) {
        this.score = score;
        this.responseBody = response.getBody();
        this.responseHttpStatus = response.getStatusCode().value();
    }

    public int getScore() {
        return score;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public int getResponseHttpStatus() {
        return responseHttpStatus;
    }
}
