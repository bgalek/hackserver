package pl.allegro.tech.leaders.hackathon.challenge.api;

import java.util.Map;

public class ChallengeTaskResult {
    private final String taskName;
    private final Map<String, String> taskParams;
    private final int maxPoints;
    private final int points;

    public ChallengeTaskResult(String taskName, Map<String, String> taskParams, int maxPoints, int points) {
        this.taskName = taskName;
        this.taskParams = taskParams;
        this.maxPoints = maxPoints;
        this.points = points;
    }

    public boolean isSolutionValid() {
        return points == maxPoints;
    }

    public String getTaskName() {
        return taskName;
    }

    public Map<String, String> getTaskParams() {
        return taskParams;
    }

    public int getMaxPoints() {
        return maxPoints;
    }

    public int getPoints() {
        return points;
    }
}
