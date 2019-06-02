package pl.allegro.tech.leaders.hackathon.challenge.api;

import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import org.springframework.util.MultiValueMap;
import pl.allegro.tech.leaders.hackathon.challenge.ChallengeDefinition;
import pl.allegro.tech.leaders.hackathon.challenge.TaskDefinition.TaskWithFixedResult;

import java.time.Instant;
import java.util.List;

public class ChallengeDetails {
    private final String id;
    private final boolean active;
    private final Instant activatedAt;
    private final String name;
    private final String description;
    private final String challengeEndpoint;
    private final List<ChallengeDefinition.QueryParam> challengeParameters;
    private final JsonSchema challengeResponse;
    private final int tasksCount;
    private final int maxPoints;
    private final Example example;

    public ChallengeDetails(
            String id,
            boolean active,
            Instant activatedAt,
            String name,
            String description,
            String challengeEndpoint,
            List<ChallengeDefinition.QueryParam> challengeParameters,
            JsonSchema challengeResponse,
            int tasksCount,
            int maxPoints,
            TaskWithFixedResult exampleTaskDefinition) {
        this.id = id;
        this.active = active;
        this.activatedAt = activatedAt;
        this.name = name;
        this.description = description;
        this.challengeEndpoint = challengeEndpoint;
        this.challengeParameters = challengeParameters;
        this.challengeResponse = challengeResponse;
        this.tasksCount = tasksCount;
        this.maxPoints = maxPoints;
        this.example = new Example(exampleTaskDefinition.getParameters(), exampleTaskDefinition.getExpectedSolution());
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

    public List<ChallengeDefinition.QueryParam> getChallengeParameters() {
        return challengeParameters;
    }

    public JsonSchema getChallengeResponse() {
        return challengeResponse;
    }

    public Example getExample() {
        return example;
    }

    public int getTasksCount() {
        return tasksCount;
    }

    public int getMaxPoints() {
        return maxPoints;
    }

    private static class Example {

        private final MultiValueMap<String, String> parameters;
        private final Object expectedSolution;

        Example(MultiValueMap<String, String> parameters, Object expectedSolution) {
            this.parameters = parameters;
            this.expectedSolution = expectedSolution;
        }

        public MultiValueMap<String, String> getParameters() {
            return parameters;
        }

        public Object getExpectedSolution() {
            return expectedSolution;
        }
    }
}
