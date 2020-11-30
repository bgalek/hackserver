package pl.allegro.tech.leaders.hackathon.challenge.samples;

import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import pl.allegro.tech.leaders.hackathon.challenge.ChallengeDefinition;
import pl.allegro.tech.leaders.hackathon.challenge.TaskDefinition;
import pl.allegro.tech.leaders.hackathon.challenge.TaskDefinition.TaskWithFixedResult;
import pl.allegro.tech.leaders.hackathon.challenge.TaskScoring;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
class ReverseStringChallengeDefinition implements ChallengeDefinition {

    private static final List<TaskWithFixedResult> TASKS = List.of(
            TaskDefinition.withFixedResult("Should return repoleved", new LinkedMultiValueMap<>(Map.of("string", List.of("developer"))), "repoleved", new TaskScoring(5, 100)),
            TaskDefinition.withFixedResult("Should return palindorme", new LinkedMultiValueMap<>(Map.of("string", List.of("no lemon, no melon"))), "nolem on ,nomel on", new TaskScoring(5, 100)),
            TaskDefinition.withFixedResult("Should look out for utf accents", new LinkedMultiValueMap<>(Map.of("string", List.of(URLEncoder.encode("Les Misérables", StandardCharsets.UTF_8)))), "selbarésiM seL", new TaskScoring(5, 100), true),
            TaskDefinition.withFixedResult("Should look out for control characters", new LinkedMultiValueMap<>(Map.of("string", List.of(URLEncoder.encode("\uD83D\uDC4B\uD83C\uDFFF\uD83D\uDC76", StandardCharsets.UTF_8)))), "\uD83D\uDC76\uD83D\uDC4B\uD83C\uDFFF", new TaskScoring(5, 100), true)
    );

    @Override
    public String getName() {
        return "Reverse String";
    }

    @Override
    public String getDescription() {
        return "It's straightforward - return given string backwards";
    }

    @Override
    public String getChallengeEndpoint() {
        return "/reversed";
    }

    @Override
    public Class<String> solutionType() {
        return String.class;
    }

    @Override
    public List<QueryParam> getChallengeParams() {
        return List.of(
                new QueryParam("string", "input string")
        );
    }

    @Override
    public TaskDefinition getExample() {
        return TaskDefinition.withFixedResult("Should return repoleved", new LinkedMultiValueMap<>(Map.of("string", List.of("developer"))), "repoleved", new TaskScoring(5, 100));
    }

    @Override
    public List<TaskDefinition> getTasks() {
        return new ArrayList<>(TASKS);
    }
}
