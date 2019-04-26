package pl.allegro.tech.leaders.hackathon.challenge;

public class TaskScoring {
    private final int maxPoints;
    private final int latencyPenaltyFactor;

    public TaskScoring(int maxPoints, int latencyPenaltyFactor) {
        this.maxPoints = maxPoints;
        this.latencyPenaltyFactor = latencyPenaltyFactor;
    }

    public int getMaxPoints() {
        return maxPoints;
    }

    public int getLatencyPenaltyFactor() {
        return latencyPenaltyFactor;
    }
}
