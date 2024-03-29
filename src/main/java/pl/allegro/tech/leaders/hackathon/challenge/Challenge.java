package pl.allegro.tech.leaders.hackathon.challenge;

import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;
import pl.allegro.tech.leaders.hackathon.challenge.api.ChallengeDetails;

import javax.annotation.Nullable;
import java.time.Clock;
import java.time.Instant;
import java.util.Arrays;
import java.util.stream.Collectors;

class Challenge {
    private final String id;
    private final ChallengeDefinition definition;
    private boolean active;
    private Instant activatedAt;

    Challenge(ChallengeState state, ChallengeDefinition definition) {
        if (state.id == null || !state.id.equals(definition.getId())) {
            throw new IllegalArgumentException("Challenge id and definition id must be the same");
        }
        this.id = state.id;
        this.active = state.active;
        this.activatedAt = state.activatedAt;
        this.definition = definition;
    }

    Challenge(ChallengeDefinition definition) {
        this.id = definition.getId();
        this.definition = definition;
    }

    public String getId() {
        return id;
    }

    void activate(Clock clock) {
        this.active = true;
        this.activatedAt = clock.instant();
    }

    void deactivate() {
        this.active = false;
        this.activatedAt = null;
    }

    @Nullable
    Instant getActivatedAt() {
        return activatedAt;
    }

    boolean isActive() {
        return active;
    }

    ChallengeState toState() {
        return new ChallengeState(id, active, activatedAt);
    }

    ChallengeDefinition getDefinition() {
        return definition;
    }

    ChallengeDetails toChallengeDetailsDto() {
        return new ChallengeDetails(
                this.id,
                this.active,
                this.activatedAt,
                this.definition.getName(),
                Arrays.stream(this.definition.getDescription().split("\n")).collect(Collectors.toList()),
                this.definition.getChallengeEndpoint(),
                this.definition.getChallengeParams(),
                this.definition.getChallengeResponse(),
                this.definition.getTasks().size(),
                this.definition.getMaxPoints(),
                (TaskDefinition.TaskWithFixedResult) this.definition.getExample()
        );
    }

    @Document("challenges")
    @TypeAlias("challenge")
    static class ChallengeState {
        String id;
        boolean active;
        Instant activatedAt;

        ChallengeState(String id, boolean active, Instant activatedAt) {
            this.id = id;
            this.active = active;
            this.activatedAt = activatedAt;
        }
    }
}
