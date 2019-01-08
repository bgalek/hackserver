package pl.allegro.tech.leaders.hackathon.challenge.base

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.module.jsonSchema.JsonSchema
import com.fasterxml.jackson.module.jsonSchema.JsonSchemaGenerator
import groovy.transform.CompileStatic
import pl.allegro.tech.leaders.hackathon.challenge.api.Challenge

import static pl.allegro.tech.leaders.hackathon.configuration.ObjectMapperProvider.objectMapper

@CompileStatic
class SumChallenge implements Challenge {
    public static final ID = "sum"

    @Override
    String getId() {
        return ID
    }

    @Override
    String getName() {
        return "Sum Challenge"
    }

    @Override
    String getDescription() {
        return "Your task is to make sum calculator api!"
    }

    @Override
    String getChallengeEndpoint() {
        return "/sum"
    }

    @Override
    List<QueryParam> getChallengeParams() {
        return [new QueryParam("query", "equation to samples")]
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
    List<Task> getTasks() {
        return [
                new Task("2+2", "4"),
                new Task("2+2+2", "6"),
                new Task("2+2+2+2", "8")
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