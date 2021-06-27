package pl.allegro.tech.leaders.hackathon.challenge.samples;

import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import pl.allegro.tech.leaders.hackathon.challenge.ChallengeDefinition;
import pl.allegro.tech.leaders.hackathon.challenge.TaskDefinition;
import pl.allegro.tech.leaders.hackathon.challenge.TaskScoring;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.lang.String.format;

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
                    new TaskScoring(25, 1000),
                    true)
    );

    private static TaskDefinition.TaskWithFixedResult getDynamicExample() {
        SecureRandom random = new SecureRandom();
        var x = random.nextInt(50) + random.nextDouble();
        var y = random.nextInt(50) + random.nextDouble();
        var z = random.nextInt(50) + random.nextDouble();
        List<String> operationList = List.of("+", "-", "*", "/");
        var operation1 = operationList.get(random.nextInt(4));
        var operation2 = operationList.get(random.nextInt(4));

        String task = format("%.2f %s %.2f %s %.2f", x, operation1, y, operation2, z);

        ExpressionParser parser = new SpelExpressionParser();
        Expression exp = parser.parseExpression(task);

        return TaskDefinition.withFixedResult("Should also work for random cases", new LinkedMultiValueMap<>(
                        Map.of("equation", List.of(URLEncoder.encode(task, StandardCharsets.UTF_8)))),
                round(Objects.requireNonNull(exp.getValue(Double.class)), 2),
                new TaskScoring(30, 1000),
                true
        );
    }

    public static String round(double value, int places) {
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.toPlainString();
    }

    @Override
    public String getName() {
        return "Calculator";
    }

    @Override
    public String getDescription() {
        return "Your task is to write a simple calculator. " +
                "We will send you some equations, be prepared! " +
                "(don't mind any parentheses or strange mathematical symbols though)." +
                "Response should contain *up to* 2 decimal places, with trailing zeros removed." +
                "E.g 2 + 2 = 4 (not 4.0), 0.123 + 0.123 = 0.25";
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
                new QueryParam("equation", "equation to calc")
        );
    }

    @Override
    public TaskDefinition getExample() {
        return TaskDefinition.withFixedResult("Should calculate a sum 2+2=4",
                new LinkedMultiValueMap<>(Map.of("equation", List.of("2%2B2"))),
                "4",
                new TaskScoring(4, 100),
                true
        );
    }

    @Override
    public List<TaskDefinition> getTasks() {
        var res = new ArrayList<>(TASKS);
        res.add(getDynamicExample());
        return res;
    }
}
