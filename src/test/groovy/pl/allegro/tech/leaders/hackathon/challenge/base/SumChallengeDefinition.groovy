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
class SumChallengeDefinition implements ChallengeDefinition {
    public static final ID = "sum"
    public static final ChallengeTask FIRST_TASK = ChallengeTask.withFixedResult("Should sum 2 numbers to 5", [equation: "2+3"], "5")
    public static final ChallengeTask SECOND_TASK = ChallengeTask.withFixedResult("Should sum 3 numbers to 9", [equation: "2+3+4"], "9")
    public static final ChallengeTask THIRD_TASK = ChallengeTask.withFixedResult("Should sum 4 numbers to 14", [equation: "2+3+4+5"], "14")

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
            return new JsonSchemaGenerator(objectMapper()).generateSchema(SumChallengeResponse)
        } catch (JsonMappingException e) {
            throw new RuntimeException("Could not create schema", e)
        }
    }

    @Override
    List<ChallengeTask> getTasks() {
        return [FIRST_TASK, SECOND_TASK, THIRD_TASK]
    }

    @Override
    List<String> getExamples() {
        return ["1+1", "2+2+2"]
    }

    static class SumChallengeResponse {
        @JsonProperty(required = true)
        int result
    }
}