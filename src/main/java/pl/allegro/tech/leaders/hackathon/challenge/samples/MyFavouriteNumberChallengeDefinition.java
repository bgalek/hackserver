package pl.allegro.tech.leaders.hackathon.challenge.samples;

import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import pl.allegro.tech.leaders.hackathon.challenge.ChallengeDefinition;
import pl.allegro.tech.leaders.hackathon.challenge.TaskDefinition;
import pl.allegro.tech.leaders.hackathon.challenge.TaskScoring;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Component
class MyFavouriteNumberChallengeDefinition implements ChallengeDefinition {

    public static final int FIRST_LIKED_NUMBER = 2999;
    public static final int SECOND_LIKED_NUMBER = 1969;
    private static final List<TaskDefinition> TASKS;

    static {
        long random = new Random().nextInt(2) * FIRST_LIKED_NUMBER + new Random().nextInt(2) * SECOND_LIKED_NUMBER + new Random().nextInt(2) + new Random().nextInt(2);
        TASKS = List.of(
                TaskDefinition.withDynamicResult("Is 2772649 my favourite number?",
                        new LinkedMultiValueMap<>(Map.of("number", List.of(() -> "2772649"))),
                        () -> "true",
                        new TaskScoring(5, 1000)
                ),
                TaskDefinition.withDynamicResult("Is 12341 my favourite number?",
                        new LinkedMultiValueMap<>(Map.of("number", List.of(() -> "12341"))),
                        () -> "false",
                        new TaskScoring(5, 1000)
                ),
                TaskDefinition.withDynamicResult("Is this my favourite number?",
                        new LinkedMultiValueMap<>(Map.of("number", List.of(() -> String.valueOf(random)))),
                        () -> String.valueOf(solve(random)),
                        new TaskScoring(10, 1000)
                ),
                TaskDefinition.withFixedResult("Is 188994228 my favourite number?",
                        new LinkedMultiValueMap<>(Map.of("number", List.of(String.valueOf(SECOND_LIKED_NUMBER * 56116 + FIRST_LIKED_NUMBER * 26176)))),
                        "true",
                        new TaskScoring(20, 1000)
                ),
                TaskDefinition.withDynamicResult("Is 23562562346363452966462312372834658762543 my favourite number?",
                        new LinkedMultiValueMap<>(Map.of("number", List.of(() -> "23562562346363452966462312372834658762543"))),
                        () -> String.valueOf(solve(new BigDecimal("23562562346363452966462312372834658762543"))),
                        new TaskScoring(30, 1000)
                )
        );
    }

    @Override
    public String getName() {
        return "My favourite number";
    }

    @Override
    public String getDescription() {
        return """
                    I like two numbers: %s (this is the year in first Futurama Episode) and %s (Linus Torvalds birth year).
                    A number N is my truly favourite number only if N is equal to the sum of certain number of %1$s and sum of certain number of %2$s.
                    Your task is to answer - is given number my truly favourite?
                    Please return "true" or "false".
                """.formatted(FIRST_LIKED_NUMBER, SECOND_LIKED_NUMBER);
    }

    @Override
    public String getChallengeEndpoint() {
        return "/favouriteNumber";
    }

    @Override
    public Class<String> solutionType() {
        return String.class;
    }

    @Override
    public List<QueryParam> getChallengeParams() {
        return List.of(
                new QueryParam("number", "number to check")
        );
    }

    @Override
    public TaskDefinition getExample() {
        return TaskDefinition.withFixedResult(
                "is 7967 my favourite number?",
                new LinkedMultiValueMap<>(Map.of("number", List.of("7967"))),
                "true",
                new TaskScoring(4, 200)
        );
    }

    @Override
    public List<TaskDefinition> getTasks() {
        return TASKS;
    }

    static boolean solve(long number) {
        return solve(BigDecimal.valueOf(number));
    }

    static boolean solve(BigDecimal number) {
        BigDecimal first = BigDecimal.valueOf(FIRST_LIKED_NUMBER);
        BigDecimal second = BigDecimal.valueOf(SECOND_LIKED_NUMBER);
        for (BigDecimal i = BigDecimal.ONE; i.compareTo(number.divide(first, RoundingMode.DOWN)) < 0; i = i.add(BigDecimal.ONE)) {
            if (number.subtract(i.multiply(first)).remainder(second).equals(BigDecimal.ZERO)) {
                return true;
            }
        }
        return false;
    }
}
