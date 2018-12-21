package pl.allegro.tech.leaders.hackathon.challenge.calc;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.fasterxml.jackson.module.jsonSchema.JsonSchemaGenerator;
import org.springframework.stereotype.Component;
import pl.allegro.tech.leaders.hackathon.challenge.Challenge;

import java.util.List;

@Component
class Calc implements Challenge {

    private final ObjectMapper objectMapper;

    Calc(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public String getName() {
        return "Calculator Challange";
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
        return List.of(new QueryParam("query", "equation to calc"));
    }

    @Override
    public JsonSchema getChallengeResponse() throws JsonMappingException {
        JsonSchemaGenerator schemaGen = new JsonSchemaGenerator(objectMapper);
        return schemaGen.generateSchema(ChalleengeResponse.class);
    }

    @Override
    public List<String> getExamples() {
        return List.of("1+1", "2+2*2", "(3+3)/3)");
    }

    private class ChalleengeResponse {

        @JsonProperty(required = true)
        private String result;

        String getResult() {
            return result;
        }
    }
}
