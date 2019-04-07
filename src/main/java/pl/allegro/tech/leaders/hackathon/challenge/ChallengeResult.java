package pl.allegro.tech.leaders.hackathon.challenge;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.http.ResponseEntity;
import pl.allegro.tech.leaders.hackathon.challenge.api.TaskResult;

import java.time.Instant;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

@Document("results")
@TypeAlias("result")
class ChallengeResult {
    @Id
    private final ChallengeResultId id;
    private final Instant executedAt;
    private final int score;
    private final String errorMessage;
    private final String responseBody;
    private final int responseHttpStatus;
    private final long latencyMillis;

    ChallengeResult(ChallengeResultId id, Instant executedAt, int score, long latencyMillis, String errorMessage, String responseBody, int responseHttpStatus) {
        this.id = requireNonNull(id);
        this.executedAt = executedAt;
        this.score = score;
        this.responseBody = responseBody;
        this.responseHttpStatus = responseHttpStatus;
        this.latencyMillis = latencyMillis;
        this.errorMessage = errorMessage;
    }

    public ChallengeResultId getId() {
        return id;
    }

    @Override
    public String toString() {
        return "ChallengeTaskResultId {" +
                "  id='" + id + '\'' +
                ", executedAt='" + executedAt + '\'' +
                ", score=" + score +
                ", responseBody='" + responseBody + '\'' +
                ", responseHttpStatus=" + responseHttpStatus +
                ", latencyMillis=" + latencyMillis +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }

    TaskResult toTaskResult() {
        return new TaskResult(
                id.teamId,
                id.challengeId,
                id.taskName,
                responseBody,
                responseHttpStatus,
                score,
                latencyMillis,
                errorMessage
        );
    }

    static class ChallengeResultId {
        @Indexed
        private final String challengeId;
        private final String taskName;
        @Indexed
        private final String teamId;

        ChallengeResultId(String teamId, String challengeId, String taskName) {
            this.teamId = requireNonNull(teamId);
            this.challengeId = requireNonNull(challengeId);
            this.taskName = requireNonNull(taskName);
        }

        public String getChallengeId() {
            return challengeId;
        }

        public String getTeamId() {
            return teamId;
        }

        public String getTaskName() {
            return taskName;
        }

        @Override
        public String toString() {
            return "ChallengeResultId {" +
                    "  challengeId='" + challengeId + '\'' +
                    ", teamId='" + teamId + '\'' +
                    ", taskName='" + taskName + '\'' +
                    "}";
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            ChallengeResultId that = (ChallengeResultId) o;
            return Objects.equals(challengeId, that.challengeId) &&
                    Objects.equals(teamId, that.teamId) &&
                    Objects.equals(taskName, that.taskName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(challengeId, teamId, taskName);
        }
    }

    static ChallengeResultBuilder builder(String teamId, String challengeId, String taskName) {
        return new ChallengeResultBuilder(teamId, challengeId, taskName);
    }

    static class ChallengeResultBuilder {
        private ChallengeResultId id;
        private Instant executedAt;
        private int score = 0;
        private String errorMessage = null;
        private String responseBody = null;
        private int responseHttpStatus;
        private long latencyMillis;

        ChallengeResultBuilder(String teamId, String challengeId, String taskName) {
            this.id = new ChallengeResultId(teamId, challengeId, taskName);
        }

        ChallengeResultBuilder executedAt(Instant executedAt) {
            this.executedAt = executedAt;
            return this;
        }

        ChallengeResultBuilder withScore(int score) {
            this.score = score;
            return this;
        }

        ChallengeResultBuilder withErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
            return this;
        }

        ChallengeResultBuilder withResponse(ResponseEntity<String> response) {
            this.responseBody = response.getBody();
            this.responseHttpStatus = response.getStatusCodeValue();
            return this;
        }

        ChallengeResultBuilder withLatency(long latencyMillis) {
            this.latencyMillis = latencyMillis;
            return this;
        }

        ChallengeResult buildErrorResult(String errorMessage) {
            return this.withErrorMessage(errorMessage)
                    .build();
        }

        ChallengeResult buildSuccessResult(int score) {
            return this.withScore(score)
                    .build();
        }

        ChallengeResult build() {
            return new ChallengeResult(id, executedAt, score, latencyMillis, errorMessage, responseBody, responseHttpStatus);
        }
    }
}
