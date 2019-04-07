package pl.allegro.tech.leaders.hackathon.scores;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import pl.allegro.tech.leaders.hackathon.challenge.api.TaskResult;
import pl.allegro.tech.leaders.hackathon.scores.api.ChallengeScores;
import pl.allegro.tech.leaders.hackathon.scores.api.HackatonScores;
import pl.allegro.tech.leaders.hackathon.scores.api.TeamScore;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;
import static org.springframework.data.mongodb.core.index.IndexDirection.DESCENDING;
import static pl.allegro.tech.leaders.hackathon.scores.Scores.ChallengeIdWithTeamId.parseChallengeTeamKey;
import static pl.allegro.tech.leaders.hackathon.scores.Scores.ChallengeIdWithTeamId.toChallengeTeamKey;

@Document("scores")
@TypeAlias("scores")
class Scores {
    @Id
    private final String id;
    // Scores ares fetched by the biggest version
    @Indexed(direction = DESCENDING)
    private long version = 0;
    private final Instant createdAt;
    private final ConcurrentHashMap<String, Long> challengeScorePerTeamId;

    // required by spring data
    @PersistenceConstructor
    Scores(String id, long version, Instant createdAt, ConcurrentHashMap<String, Long> challengeScorePerTeamId) {
        this.id = id;
        this.version = version;
        this.createdAt = createdAt;
        this.challengeScorePerTeamId = challengeScorePerTeamId;
    }

    Scores(Instant createdAt) {
        this.id = UUID.randomUUID().toString();
        this.createdAt = createdAt;
        this.challengeScorePerTeamId = new ConcurrentHashMap<>();
    }

    String getId() {
        return id;
    }

    long getVersion() {
        return version;
    }

    void updateScores(TaskResult result) {
        String key = toChallengeTeamKey(result.getChallengeId(), result.getTeamId());
        challengeScorePerTeamId.merge(key, result.getScore(), (a, b) -> a + b);
    }

    HackatonScores getHackatonScores() {
        Map<String, Long> scores = new HashMap<>();
        getScoresWithParsedKey().entrySet()
                .stream()
                .forEach(entry -> scores.merge(entry.getKey().teamId, entry.getValue(), (a, b) -> a + b));
        return new HackatonScores(createdAt, toSortedTeamScores(scores));
    }

    ChallengeScores getChallengeScores(String challengeId) {
        Map<String, Long> scores = new HashMap<>();
        getScoresWithParsedKey().entrySet()
                .stream()
                .filter(entry -> entry.getKey().getChallengeId().equals(challengeId))
                .forEach(entry -> scores.merge(entry.getKey().teamId, entry.getValue(), (a, b) -> a + b));
        return new ChallengeScores(challengeId, createdAt, toSortedTeamScores(scores));
    }

    private Map<ChallengeIdWithTeamId, Long> getScoresWithParsedKey() {
        return challengeScorePerTeamId.entrySet()
                .stream()
                .collect(Collectors.toMap(entry -> parseChallengeTeamKey(entry.getKey()), Entry::getValue));
    }

    private List<TeamScore> toSortedTeamScores(Map<String, Long> scores) {
        return scores.entrySet().stream()
                .map(e -> new TeamScore(e.getKey(), e.getValue()))
                .sorted(comparing(TeamScore::getScore).reversed())
                .collect(toList());
    }

    void mergeWithPrevious(Scores previous) {
        Map<String, Long> copy = new HashMap<>(challengeScorePerTeamId);
        challengeScorePerTeamId.putAll(previous.challengeScorePerTeamId);
        challengeScorePerTeamId.putAll(copy);
        this.version = previous.version + 1;
    }

    static class ChallengeIdWithTeamId {
        private static String KEY_SEPARATOR = "#";

        // Spring data does not support mapping for objects as map keys
        // this is why we need to concatenate challengeId and teamId to a single string value
        static ChallengeIdWithTeamId parseChallengeTeamKey(String challengeTeamKey) {
            if (!challengeTeamKey.contains(KEY_SEPARATOR)) {
                throw new IllegalArgumentException("Expected key to contain the separator. Key: " + challengeTeamKey + ", separator: " + KEY_SEPARATOR);
            }
            int separatorIndex = challengeTeamKey.indexOf(KEY_SEPARATOR);
            String challengeId = challengeTeamKey.substring(0, separatorIndex);
            String teamId = challengeTeamKey.substring(separatorIndex + 1);
            return new ChallengeIdWithTeamId(challengeId, teamId);
        }

        static String toChallengeTeamKey(String challengeId, String teamId) {
            if (challengeId.contains(KEY_SEPARATOR)) {
                throw new IllegalArgumentException("Challenge id must not contain the separator. ChallengeId: " + challengeId + ", separator: " + KEY_SEPARATOR);
            }
            return challengeId + KEY_SEPARATOR + teamId;
        }

        private final String challengeId;
        private final String teamId;

        ChallengeIdWithTeamId(String challengeId, String teamId) {
            this.challengeId = challengeId;
            this.teamId = teamId;
        }

        public String getChallengeId() {
            return challengeId;
        }

        public String getTeamId() {
            return teamId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            ChallengeIdWithTeamId that = (ChallengeIdWithTeamId) o;
            return Objects.equals(challengeId, that.challengeId) &&
                    Objects.equals(teamId, that.teamId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(challengeId, teamId);
        }
    }
}
