package pl.allegro.tech.leaders.hackathon.challenge.api;

public class TaskResult {
    private final String teamId;
    private final String challengeId;
    private final String taskName;
    private final int score;
    private final String responseBody;
    private final int responseHttpStatus;
    private final long latencyMillis;
    private final String errorMessage;

    public TaskResult(String teamId, String challengeId, String taskName, String responseBody, int responseHttpStatus, int score, long latencyMillis, String errorMessage) {
        this.challengeId = challengeId;
        this.teamId = teamId;
        this.taskName = taskName;
        this.score = score;
        this.responseBody = responseBody;
        this.responseHttpStatus = responseHttpStatus;
        this.latencyMillis = latencyMillis;
        this.errorMessage = errorMessage;
    }

    public String getTeamId() {
        return teamId;
    }

    public String getChallengeId() {
        return challengeId;
    }

    public long getScore() {
        return score;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public int getResponseHttpStatus() {
        return responseHttpStatus;
    }

    public long getLatencyMillis() {
        return latencyMillis;
    }

    public String getTaskName() {
        return taskName;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public String toString() {
        return "TaskResult{" +
                "  teamId='" + teamId + '\'' +
                "  challengeId='" + challengeId + '\'' +
                ", taskName='" + taskName + '\'' +
                ", score=" + score +
                ", responseBody='" + responseBody + '\'' +
                ", responseHttpStatus=" + responseHttpStatus +
                ", latencyMillis=" + latencyMillis +
                '}';
    }
}
