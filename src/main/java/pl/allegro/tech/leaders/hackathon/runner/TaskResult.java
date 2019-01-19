package pl.allegro.tech.leaders.hackathon.runner;

public class TaskResult {
    private final String responseBody;
    private final Integer httpStatus;
    private final long latencyInMillis;
    private final int score;

    public TaskResult(String responseBody, Integer httpStatus, long latencyInMillis, int score) {
        this.responseBody = responseBody;
        this.httpStatus = httpStatus;
        this.latencyInMillis = latencyInMillis;
        this.score = score;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public Integer getHttpStatus() {
        return httpStatus;
    }

    public long getLatencyInMillis() {
        return latencyInMillis;
    }

    public int getScore() {
        return score;
    }
}
