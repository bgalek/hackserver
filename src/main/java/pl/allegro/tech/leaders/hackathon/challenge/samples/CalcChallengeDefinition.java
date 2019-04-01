package pl.allegro.tech.leaders.hackathon.challenge.samples;

import org.springframework.stereotype.Component;
import pl.allegro.tech.leaders.hackathon.challenge.ChallengeDefinition;
import pl.allegro.tech.leaders.hackathon.challenge.TaskDefinition;
import pl.allegro.tech.leaders.hackathon.challenge.TaskDefinition.TaskWithFixedResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class CalcChallengeDefinition implements ChallengeDefinition {
    public static final String ID = "calc";

    public static final List<TaskWithFixedResult> TASKS = List.of(
            TaskDefinition.withFixedResult("Should calculate a sum 2+2=4", Map.of("equation", "2+2"), "4", 4),
            TaskDefinition.withFixedResult("Should calculate a sum and multiplication 2+2*2=6", Map.of("equation", "2+2*2"), "6", 5),
            TaskDefinition.withFixedResult("Should respect parenthesis in equation (2+2)*2 = 8", Map.of("equation", "(2+2)*2"), "8", 5)
    );

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public String getName() {
        return "Calculator Challenge";
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
        return TaskDefinition.withFixedResult("Should calculate a sum 2+2=4", Map.of("equation", "2+2"), "4", 4);
    }

    @Override
    public List<TaskDefinition> getTasks() {
        return new ArrayList<>(TASKS);
    }
}
