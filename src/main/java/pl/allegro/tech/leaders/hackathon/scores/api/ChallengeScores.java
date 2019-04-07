package pl.allegro.tech.leaders.hackathon.scores.api;

import java.time.Instant;
import java.util.List;

public class ChallengeScores {
    private final String challengeId;
    private final Instant updatedAt;
    private final List<TeamScore> scores;

    public ChallengeScores(String challengeId, Instant updatedAt, List<TeamScore> scores) {
        this.challengeId = challengeId;
        this.updatedAt = updatedAt;
        this.scores = scores;
    }

    public String getChallengeId() {
        return challengeId;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public List<TeamScore> getScores() {
        return scores;
    }
}
