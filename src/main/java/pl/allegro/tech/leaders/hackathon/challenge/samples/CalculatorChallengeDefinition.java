package pl.allegro.tech.leaders.hackathon.challenge.samples;

import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import pl.allegro.tech.leaders.hackathon.challenge.ChallengeDefinition;
import pl.allegro.tech.leaders.hackathon.challenge.TaskDefinition;
import pl.allegro.tech.leaders.hackathon.challenge.TaskDefinition.TaskWithFixedResult;
import pl.allegro.tech.leaders.hackathon.challenge.TaskScoring;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
class CalculatorChallengeDefinition implements ChallengeDefinition {

    private static final List<TaskWithFixedResult> TASKS = List.of(
            TaskDefinition.withFixedResult("Should calculate a sum 2+2=4", new LinkedMultiValueMap<>(Map.of("equation", List.of("2+2"))), "4", new TaskScoring(4, 0)),
            TaskDefinition.withFixedResult("Should calculate a sum and multiplication 2+2*2=6", new LinkedMultiValueMap<>(Map.of("equation", List.of("2+2*2"))), "6", new TaskScoring(5, 0)),
            TaskDefinition.withFixedResult("Should respect parenthesis in equation (2+2)*2 = 8", new LinkedMultiValueMap<>(Map.of("equation", List.of("(2+2)*2"))), "8", new TaskScoring(5, 0))
    );

    @Override
    public String getName() {
        return "Calculator";
    }

    @Override
    public String getDescription() {
        return "Your task is to write a simple calculator!";
    }

    @Override
    public String getChallengeEndpoint() {
        return "/calc";
    }

    @Override
    public Class<String> solutionType() {
        return String.class;
    }

    @Override
    public List<QueryParam> getChallengeParams() {
        return List.of(
                new QueryParam("query", "equation to calc")
        );
    }

    @Override
    public TaskDefinition getExample() {
        return TaskDefinition.withFixedResult("Should calculate a sum 2+2=4", new LinkedMultiValueMap<>(Map.of("query", List.of("2+2"))), "4", new TaskScoring(4, 200));
    }

    @Override
    public List<TaskDefinition> getTasks() {
        return new ArrayList<>(TASKS);
    }
}
