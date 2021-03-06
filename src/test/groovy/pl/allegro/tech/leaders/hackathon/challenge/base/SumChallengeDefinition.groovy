package pl.allegro.tech.leaders.hackathon.challenge.base

import groovy.transform.CompileStatic
import org.springframework.util.LinkedMultiValueMap
import pl.allegro.tech.leaders.hackathon.challenge.ChallengeDefinition
import pl.allegro.tech.leaders.hackathon.challenge.TaskDefinition
import pl.allegro.tech.leaders.hackathon.challenge.TaskScoring

import static pl.allegro.tech.leaders.hackathon.challenge.TaskDefinition.TaskWithFixedResult
import static pl.allegro.tech.leaders.hackathon.challenge.TaskDefinition.withFixedResult

@CompileStatic
class SumChallengeDefinition implements ChallengeDefinition {
    public static final ID = "sum"
    public static final TaskWithFixedResult FIRST_TASK = withFixedResult("Should sum 2 numbers to 5", new LinkedMultiValueMap<>([equation: ["2+3"]]), new SampleResponse("5"), new TaskScoring(1, 0))
    public static final TaskWithFixedResult SECOND_TASK = withFixedResult("Should sum 3 numbers to 9", new LinkedMultiValueMap<>([equation: ["2+3+4"]]), new SampleResponse("9"), new TaskScoring(1, 0))
    public static final TaskWithFixedResult THIRD_TASK = withFixedResult("Should sum 4 numbers to 14", new LinkedMultiValueMap<>([equation: ["2+3+4+5"]]), new SampleResponse("14"), new TaskScoring(1, 0))

    @Override
    String getId() {
        ID
    }

    @Override
    String getName() {
        "Sum Challenge"
    }

    @Override
    String getDescription() {
        "Your task is to make sum calculator api!"
    }

    @Override
    String getChallengeEndpoint() {
        "/sum"
    }

    @Override
    List<QueryParam> getChallengeParams() {
        [new QueryParam("query", "equation to samples")]
    }

    @Override
    List<TaskWithFixedResult> getTasks() {
        [FIRST_TASK, SECOND_TASK, THIRD_TASK]
    }

    @Override
    Class<?> solutionType() {
        SampleResponse
    }

    @Override
    TaskDefinition getExample() {
        return FIRST_TASK
    }
}
