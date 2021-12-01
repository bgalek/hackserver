package pl.allegro.tech.leaders.hackathon.challenge.samples;

import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import pl.allegro.tech.leaders.hackathon.challenge.ChallengeDefinition;
import pl.allegro.tech.leaders.hackathon.challenge.TaskDefinition;
import pl.allegro.tech.leaders.hackathon.challenge.TaskScoring;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Component
public class RegularExpressionChallengeDefinition implements ChallengeDefinition {

    private static final List<TaskDefinition> TASKS = List.of(
            TaskDefinition.withValidateResult("https://allegro.pl/obrazek.jpg",
                    "https://allegro.pl/obrazek.jpg",
                    RegularExpressionChallengeDefinition::validateRegex,
                    true,
                    new TaskScoring(5, 1000)),
            TaskDefinition.withValidateResult("ws://allegro.pl/jpg/obrazek.jpg?timestamp=1234567",
                    "ws://allegro.pl/jpg/obrazek.jpg?timestamp=1234567",
                    RegularExpressionChallengeDefinition::validateRegex,
                    true,
                    new TaskScoring(5, 1000)),
            TaskDefinition.withValidateResult("https://allegro-pay.pl/obrazek",
                    "https://allegro-pay.pl/obrazek",
                    RegularExpressionChallengeDefinition::validateRegex,
                    false,
                    new TaskScoring(5, 1000)),
            TaskDefinition.withValidateResult("https://allegro.pl/costam?query=obrazek.jpg",
                    "https://allegro.pl/costam?query=obrazek.jpg",
                    RegularExpressionChallengeDefinition::validateRegex,
                    false,
                    new TaskScoring(5, 1000)),
            TaskDefinition.withValidateResult("https://allegro.pl/costam?query=.jpg",
                    "https://allegro.pl/costam?query=.jpg",
                    RegularExpressionChallengeDefinition::validateRegex,
                    false,
                    new TaskScoring(5, 1000)),
            TaskDefinition.withValidateResult("http://allegro.jpg/costam",
                    "http://allegro.jpg/costam",
                    RegularExpressionChallengeDefinition::validateRegex,
                    false,
                    new TaskScoring(5, 1000)),
            TaskDefinition.withValidateResult("https://allegro.pl/cos-tam#obrazek.jpg",
                    "https://allegro.pl/cos-tam#obrazek.jpg",
                    RegularExpressionChallengeDefinition::validateRegex,
                    false,
                    new TaskScoring(5, 1000)),
            TaskDefinition.withValidateResult("https://allegro.pl/costam%20jpg",
                    "https://allegro.pl/costam%20jpg",
                    RegularExpressionChallengeDefinition::validateRegex,
                    false,
                    new TaskScoring(5, 1000)),
            TaskDefinition.withValidateResult("http://allegro.jpg.pl/allegro.jpg",
                    "http://allegro.jpg.pl/allegro.jpg",
                    RegularExpressionChallengeDefinition::validateRegex,
                    true,
                    new TaskScoring(5, 1000)),
            TaskDefinition.withValidateResult("https://allegro.pl/test//allegro.jpg",
                    "https://allegro.pl/test//allegro.jpg",
                    RegularExpressionChallengeDefinition::validateRegex,
                    true,
                    new TaskScoring(5, 1000)),
            TaskDefinition.withValidateResult("./test/allegro.jpg",
                    "./test/allegro.jpg",
                    RegularExpressionChallengeDefinition::validateRegex,
                    true,
                    new TaskScoring(5, 1000)),
            TaskDefinition.withValidateResult("./allegro.jpg",
                    "./allegro.jpg",
                    RegularExpressionChallengeDefinition::validateRegex,
                    true,
                    new TaskScoring(5, 1000)),
            TaskDefinition.withValidateResult("ftp://bartosz:galek@localhost/test/allegro.jpg",
                    "ftp://bartosz:galek@localhost/test/allegro.jpg",
                    RegularExpressionChallengeDefinition::validateRegex,
                    true,
                    new TaskScoring(5, 1000)),
            TaskDefinition.withValidateResult("smtp://bartosz:galek@localhost/test/allegro.txt.jpg",
                    "smtp://bartosz:galek@localhost/test/allegro.txt.jpg",
                    RegularExpressionChallengeDefinition::validateRegex,
                    true,
                    new TaskScoring(5, 1000)),
            TaskDefinition.withValidateResult("ftp://bartosz:galek@localhost/test/allegro.jpg.txt",
                    "ftp://bartosz:galek@localhost/test/allegro.jpg.txt",
                    RegularExpressionChallengeDefinition::validateRegex,
                    false,
                    new TaskScoring(5, 1000)),
            TaskDefinition.withValidateResult("jpg://localhost:8080/test/image.jpg",
                    "jpg://localhost:8080/test/image.jpg",
                    RegularExpressionChallengeDefinition::validateRegex,
                    true,
                    new TaskScoring(5, 1000)),
            TaskDefinition.withValidateResult("../.jpg/../../obrazek.jpg",
                    "../.jpg/../../obrazek.jpg",
                    RegularExpressionChallengeDefinition::validateRegex,
                    true,
                    new TaskScoring(5, 1000)),
            TaskDefinition.withValidateResult("http:///adres/img.jpg",
                    "http:///adres/img.jpg",
                    RegularExpressionChallengeDefinition::validateRegex,
                    true,
                    new TaskScoring(5, 1000)
            )
    );

    @Override
    public String getName() {
        return "Regular Madness";
    }

    @Override
    public String getDescription() {
        return """
                    Is finding image urls (jpg) possible using regular expressions?
                    Prepare a regular expression that matches urls pointing to an image resource.
                    Return the same expression for every given test case.
                """;
    }

    @Override
    public String getChallengeEndpoint() {
        return "/regexp";
    }

    @Override
    public List<QueryParam> getChallengeParams() {
        return List.of();
    }

    @Override
    public Class<?> solutionType() {
        return String.class;
    }

    @Override
    public TaskDefinition getExample() {
        return TaskDefinition.withFixedResult("https://allegro.pl/obrazek.jpg",
                new LinkedMultiValueMap<>(Map.of()),
                ".*\\.jpg",
                new TaskScoring(5, 1000));
    }

    @Override
    public List<TaskDefinition> getTasks() {
        return TASKS;
    }

    private static boolean validateRegex(String testcase, String expression) {
        return Pattern.compile(expression).matcher(testcase).find();
    }
}
