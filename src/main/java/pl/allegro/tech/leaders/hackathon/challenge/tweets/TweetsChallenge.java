package pl.allegro.tech.leaders.hackathon.challenge.tweets;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.fasterxml.jackson.module.jsonSchema.JsonSchemaGenerator;
import org.springframework.stereotype.Component;
import pl.allegro.tech.leaders.hackathon.challenge.Challenge;
import pl.allegro.tech.leaders.hackathon.challenge.Task;

import java.util.List;

@Component
class TweetsChallenge implements Challenge {

    private final ObjectMapper objectMapper;

    TweetsChallenge(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public String getName() {
        return "Twitter Challenge";
    }

    @Override
    public String getDescription() {
        return "Your task is to show me my tweets!";
    }

    @Override
    public String getChallengeEndpoint() {
        return "/tweets";
    }

    @Override
    public List<QueryParam> getChallengeParams() {
        return List.of(new QueryParam("user", "user login"));
    }

    @Override
    public JsonSchema getChallengeResponse() throws JsonMappingException {
        JsonSchemaGenerator schemaGen = new JsonSchemaGenerator(objectMapper);
        return schemaGen.generateSchema(ChallengeResponse.class);
    }

    @Override
    public List<String> getExamples() {
        return List.of("[]", "[{\"content\":\"this is my tweet!\"}]");
    }

    @Override
    public List<Task> getTasks() {
        return null;
    }

    private class ChallengeResponse {

        @JsonProperty(required = true)
        private String result;

        String getResult() {
            return result;
        }
    }
}
