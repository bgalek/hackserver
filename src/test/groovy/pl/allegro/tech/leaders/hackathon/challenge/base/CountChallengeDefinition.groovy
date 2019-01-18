package pl.allegro.tech.leaders.hackathon.challenge.base

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.module.jsonSchema.JsonSchema
import com.fasterxml.jackson.module.jsonSchema.JsonSchemaGenerator
import groovy.transform.CompileStatic
import pl.allegro.tech.leaders.hackathon.challenge.api.ChallengeDefinition
import pl.allegro.tech.leaders.hackathon.challenge.api.ChallengeTask

import static pl.allegro.tech.leaders.hackathon.configuration.ObjectMapperProvider.objectMapper

@CompileStatic
class CountChallengeDefinition implements ChallengeDefinition {
    public static final ID = "count"

    @Override
    String getId() {
        return ID
    }

    @Override
    String getName() {
        return "Count string occurrences"
    }

    @Override
    String getDescription() {
        return "Your task is to count string occurrences!"
    }

    @Override
    String getChallengeEndpoint() {
        return "/count"
    }

    @Override
    List<QueryParam> getChallengeParams() {
        return [
                new QueryParam("text", "sample text to check"),
                new QueryParam("phrase", "sample text to count in the text")
        ]
    }

    @Override
    JsonSchema getChallengeResponse() {
        try {
            return new JsonSchemaGenerator(objectMapper()).generateSchema(ChallengeResponse)
        } catch (JsonMappingException e) {
            throw new RuntimeException("Could not create schema", e)
        }
    }

    @Override
    List<ChallengeTask> getTasks() {
        return [
                ChallengeTask.withFixedResult("Should find two occurrences", [text: "abba", token: "b"], "2"),
                ChallengeTask.withFixedResult("Should find one occurrence", [text: "alphabet", token: "b"], "1")
        ]
    }

    @Override
    List<String> getExamples() {
        return ["1+1", "2+2+2"]
    }

    static class ChallengeResponse {
        @JsonProperty(required = true)
        String result
    }
}