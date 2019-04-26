package pl.allegro.tech.leaders.hackathon.scores.api;

import java.util.Objects;

public class TeamScore {
    private final String teamId;
    private final long score;

    public TeamScore(String teamId, long score) {
        this.teamId = teamId;
        this.score = score;
    }

    public String getTeamId() {
        return teamId;
    }

    public long getScore() {
        return score;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TeamScore)) {
            return false;
        }
        TeamScore teamScore = (TeamScore) o;
        return score == teamScore.score &&
                Objects.equals(teamId, teamScore.teamId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamId, score);
    }

    @Override
    public String toString() {
        return "TeamScore{" +
                "teamId='" + teamId + '\'' +
                ", score=" + score +
                '}';
    }
}
