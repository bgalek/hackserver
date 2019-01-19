package pl.allegro.tech.leaders.hackathon.challenge;

import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;
import pl.allegro.tech.leaders.hackathon.challenge.api.ChallengeDetails;

import java.time.Clock;
import java.time.Instant;

class Challenge {
    private final String id;
    private boolean active;
    private Instant activatedAt;
    private final ChallengeDefinition definition;

    Challenge(ChallengeState state, ChallengeDefinition definition) {
        if (state.id == null || !state.id.equals(definition.getId())) {
            throw new IllegalArgumentException("Challenge id and definition id be the same");
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
                this.definition.getDescription(),
                this.definition.getChallengeEndpoint(),
                this.definition.getChallengeParams(),
                this.definition.getChallengeResponse()
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
