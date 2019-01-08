package pl.allegro.tech.leaders.hackathon.challenge.samples;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.fasterxml.jackson.module.jsonSchema.JsonSchemaGenerator;
import org.springframework.stereotype.Component;
import pl.allegro.tech.leaders.hackathon.challenge.api.Challenge;

import java.util.List;

@Component
class CalcChallenge implements Challenge {

    private static final String LEVEL1 = "2+2";
    private static final String LEVEL2 = "2+2*2";
    private static final String LEVEL3 = "(2+2)*2";

    private final ObjectMapper objectMapper;
    private final List<Task> tasks = List.of(
            new Task(LEVEL1, "4"),
            new Task(LEVEL2, "6"),
            new Task(LEVEL3, "8")
    );

    CalcChallenge(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public String getName() {
        return "Calculator Challenge";
    }

    @Override
    public String getDescription() {
        return "Your task is to make calculator api!";
    }

    @Override
    public String getChallengeEndpoint() {
        return "/calc";
    }

    @Override
    public List<QueryParam> getChallengeParams() {
        return List.of(
                new QueryParam("query", "equation to calc")
        );
    }

    @Override
    public JsonSchema getChallengeResponse() {
        JsonSchemaGenerator schemaGen = new JsonSchemaGenerator(objectMapper);
        try {
            return schemaGen.generateSchema(ChallengeResponse.class);
        } catch (JsonMappingException e) {
            throw new RuntimeException("Could not create schema", e);
        }
    }

    public List<Task> getTasks() {
        return this.tasks;
    }

    @Override
    public List<String> getExamples() {
        return List.of("1+1", "2+2*2", "(3+3)/3)");
    }

    private class ChallengeResponse {

        @JsonProperty(required = true)
        private String result;

        String getResult() {
            return result;
        }
    }
}
