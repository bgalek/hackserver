package pl.allegro.tech.leaders.hackathon.challenge.samples;

import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import pl.allegro.tech.leaders.hackathon.challenge.ChallengeDefinition;
import pl.allegro.tech.leaders.hackathon.challenge.TaskDefinition;
import pl.allegro.tech.leaders.hackathon.challenge.TaskDefinition.TaskWithDynamicResult;
import pl.allegro.tech.leaders.hackathon.challenge.TaskScoring;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Component
class CalculatorChallengeDefinition implements ChallengeDefinition {

    private static final List<TaskDefinition> TASKS = List.of(
            TaskDefinition.withFixedResult("Should calculate simple sum",
                    new LinkedMultiValueMap<>(Map.of("equation", List.of("2%2B2"))),
                    "4",
                    new TaskScoring(5, 1000),
                    true),
            TaskDefinition.withFixedResult("Should calculate with negative numbers",
                    new LinkedMultiValueMap<>(Map.of("equation", List.of("-2%2B2"))),
                    "0",
                    new TaskScoring(10, 1000),
                    true),
            TaskDefinition.withFixedResult("Should calculate fractions",
                    new LinkedMultiValueMap<>(Map.of("equation", List.of("0.1%2B0.1"))),
                    "0.2",
                    new TaskScoring(20, 1000),
                    true),
            TaskDefinition.withFixedResult("Should calculate numbers with leading (insignificant) zeros",
                    new LinkedMultiValueMap<>(Map.of("equation", List.of("000001%2B000002"))),
                    "3",
                    new TaskScoring(20, 1000),
                    true),
            TaskDefinition.withFixedResult("Should calculate sum of big numbers",
                    new LinkedMultiValueMap<>(Map.of("equation", List.of("423420034234%2B312435324423"))),
                    "735855358657",
                    new TaskScoring(20, 1000),
                    true),
            TaskDefinition.withFixedResult("Should calculate respecting sequence of actions order",
                    new LinkedMultiValueMap<>(Map.of("equation", List.of("2%2B2*2"))),
                    "6",
                    new TaskScoring(30, 1000),
                    true)
            // not so dynamic
//            getDynamicExample("Should also work for random cases")
    );

    private static TaskWithDynamicResult getDynamicExample(String name) {
        Random random = new Random();
        var x = new Random().nextInt(50);
        var y = random.nextInt(50);
        var z = random.nextInt(50);
        List<String> operationList = List.of("%2B", "-", "%2A", "%2F");
        var operation1 = operationList.get(random.nextInt(4));
        var operation12 = operationList.get(random.nextInt(4));
        ExpressionParser parser = new SpelExpressionParser();
        Expression exp = parser.parseExpression(String.format("%d %s %d %s %d", x, operation1, y, operation12, z));
        return TaskDefinition.withDynamicResult(name, new LinkedMultiValueMap<>(
                        Map.of("equation", List.of(() -> String.format("%d %s %d %s %d", x, operation1, y, operation12, z)))),
                exp::getValue,
                new TaskScoring(50, 1000),
                true
        );
    }

    @Override
    public String getName() {
        return "Calculator";
    }

    @Override
    public String getDescription() {
        return "Your task is to write a simple calculator. " +
                "We will send you some equations, be prepared! " +
                "(don't mind any parentheses or strange mathematical symbols though)";
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
        return TaskDefinition.withFixedResult("Should calculate a sum 2+2=4", new LinkedMultiValueMap<>(Map.of("equation", List.of("2%2B2"))), "4", new TaskScoring(4, 100), true);
    }

    @Override
    public List<TaskDefinition> getTasks() {
        return new ArrayList<>(TASKS);
    }
}
