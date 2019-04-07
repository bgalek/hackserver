package pl.allegro.tech.leaders.hackathon.scores.api;

import java.time.Instant;
import java.util.List;

public class HackatonScores {
    private final Instant updatedAt;
    private final List<TeamScore> scores;

    public HackatonScores(Instant updatedAt, List<TeamScore> scores) {
        this.updatedAt = updatedAt;
        this.scores = scores;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public List<TeamScore> getScores() {
        return scores;
    }
}
