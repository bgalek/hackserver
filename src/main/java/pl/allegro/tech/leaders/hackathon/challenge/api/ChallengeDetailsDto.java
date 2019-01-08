package pl.allegro.tech.leaders.hackathon.challenge.api;

import com.fasterxml.jackson.module.jsonSchema.JsonSchema;

import java.util.List;

public class ChallengeDetailsDto {
    private final String id;
    private final String name;
    private final String description;
    private final String challengeEndpoint;
    private final List<Challenge.QueryParam> challengeParams;
    private final JsonSchema challengeResponse;
    private final List<String> examples;

    public ChallengeDetailsDto(
            String id,
            String name,
            String description,
            String challengeEndpoint,
            List<Challenge.QueryParam> challengeParams,
            JsonSchema challengeResponse,
            List<String> examples) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.challengeEndpoint = challengeEndpoint;
        this.challengeParams = challengeParams;
        this.challengeResponse = challengeResponse;
        this.examples = examples;
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

    public List<Challenge.QueryParam> getChallengeParams() {
        return challengeParams;
    }

    public JsonSchema getChallengeResponse() {
        return challengeResponse;
    }

    public List<String> getExamples() {
        return examples;
    }
}
