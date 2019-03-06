package pl.allegro.tech.leaders.hackathon.challenge.base

import groovy.transform.CompileStatic
import pl.allegro.tech.leaders.hackathon.challenge.ChallengeDefinition
import pl.allegro.tech.leaders.hackathon.challenge.TaskDefinition

import static pl.allegro.tech.leaders.hackathon.challenge.TaskDefinition.TaskWithFixedResult
import static pl.allegro.tech.leaders.hackathon.challenge.TaskDefinition.withFixedResult

@CompileStatic
class CountChallengeDefinition implements ChallengeDefinition {
    public static final ID = "count"
    public static TaskWithFixedResult COUNT_FIRST_TASK = withFixedResult("Should find two occurrences", [text: "abba", token: "b"], new SampleResponse("2"), 1)
    public static TaskWithFixedResult COUNT_SECOND_TASK = withFixedResult("Should find one occurrence", [text: "alphabet", token: "b"], new SampleResponse("1"), 1)

    @Override
    String getId() {
        ID
    }

    @Override
    String getName() {
        "Count string occurrences"
    }

    @Override
    String getDescription() {
        "Your task is to count string occurrences!"
    }

    @Override
    String getChallengeEndpoint() {
        "/count"
    }

    @Override
    List<QueryParam> getChallengeParams() {
        [
                new QueryParam("text", "sample text to check"),
                new QueryParam("phrase", "sample text to count in the text")
        ]
    }

    @Override
    List<TaskDefinition> getTasks() {
        return [COUNT_FIRST_TASK, COUNT_SECOND_TASK] as List<TaskDefinition>
    }

    @Override
    Class<?> solutionType() {
        SampleResponse
    }
}
