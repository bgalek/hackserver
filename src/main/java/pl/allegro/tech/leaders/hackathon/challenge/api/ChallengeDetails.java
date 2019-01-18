package pl.allegro.tech.leaders.hackathon.challenge.api;

import com.fasterxml.jackson.module.jsonSchema.JsonSchema;

import java.time.Instant;
import java.util.List;

public class ChallengeDetails {
    private final String id;
    private final boolean active;
    private final Instant activatedAt;
    private final String name;
    private final String description;
    private final String challengeEndpoint;
    private final List<ChallengeDefinition.QueryParam> challengeParams;
    private final JsonSchema challengeResponse;
    private final List<String> examples;

    public ChallengeDetails(
            String id,
            boolean active,
            Instant activatedAt,
            String name,
            String description,
            String challengeEndpoint,
            List<ChallengeDefinition.QueryParam> challengeParams,
            JsonSchema challengeResponse,
            List<String> examples) {
        this.id = id;
        this.active = active;
        this.activatedAt = activatedAt;
        this.name = name;
        this.description = description;
        this.challengeEndpoint = challengeEndpoint;
        this.challengeParams = challengeParams;
        this.challengeResponse = challengeResponse;
        this.examples = examples;
    }

    public boolean isActive() {
        return active;
    }

    public Instant getActivatedAt() {
        return activatedAt;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getChallengeEndpoint() {
        return challengeEndpoint;
    }

    public List<ChallengeDefinition.QueryParam> getChallengeParams() {
        return challengeParams;
    }

    public JsonSchema getChallengeResponse() {
        return challengeResponse;
    }

    public List<String> getExamples() {
        return examples;
    }
}
