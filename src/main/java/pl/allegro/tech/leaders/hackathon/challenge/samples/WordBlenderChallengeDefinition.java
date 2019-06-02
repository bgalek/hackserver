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
class WordBlenderChallengeDefinition implements ChallengeDefinition {
    private static final List<TaskWithFixedResult> TASKS = List.of(
            TaskDefinition.withFixedResult(
                    "Should be able to create 2 whole words",
                    new LinkedMultiValueMap<>(Map.of("word", List.of(
                            "devday",
                            "code",
                            "hacker",
                            "java",
                            "danger",
                            "allegro",
                            "paperclip",
                            "lullaby"
                    ))),
                    2,
                    new TaskScoring(4, 200)
            ),
            TaskDefinition.withFixedResult(
                    "Should be able to create 4 whole words",
                    new LinkedMultiValueMap<>(Map.of("word", List.of(
                            "presentation",
                            "slave",
                            "colorful",
                            "hospitality",
                            "knot",
                            "staircase",
                            "catalogue",
                            "announcement",
                            "quarrel",
                            "first",
                            "criticism",
                            "reservoir",
                            "incident",
                            "switch",
                            "frog",
                            "link",
                            "cassette",
                            "provision",
                            "holiday",
                            "hard",
                            "hypnothize",
                            "evaluate",
                            "royalty",
                            "deviation",
                            "available"
                    ))),
                    4,
                    new TaskScoring(4, 200)
            ),
            TaskDefinition.withFixedResult(
                    "Should be able to create 5 whole words",
                    new LinkedMultiValueMap<>(Map.of("word", List.of(
                            "developer",
                            "reedlvoep",
                            "vrpoleeed",
                            "derelveop",
                            "developer"
                    ))),
                    5,
                    new TaskScoring(4, 200)
            )
    );

    @Override
    public String getName() {
        return "Word Blender";
    }

    @Override
    public String getDescription() {
        return "You will receive a set of words, that will be placed into high power word blender. " +
                "Your task is to count how many times word \"developer\" can be created simply by " +
                "taking the letters form the blender one by one. Remaining letters will be used another time.";
    }

    @Override
    public String getChallengeEndpoint() {
        return "/blender";
    }

    @Override
    public Class<Integer> solutionType() {
        return Integer.class;
    }

    @Override
    public List<QueryParam> getChallengeParams() {
        return List.of(
                new QueryParam("word", "word to blend")
        );
    }

    @Override
    public TaskDefinition getExample() {
        return TaskDefinition.withFixedResult(
                "8 words",
                new LinkedMultiValueMap<>(Map.of("word", List.of(
                        "devday",
                        "code",
                        "hacker",
                        "java",
                        "danger",
                        "allegro",
                        "paperclip",
                        "lullaby"
                ))),
                2,
                new TaskScoring(4, 200)
        );
    }

    @Override
    public List<TaskDefinition> getTasks() {
        return new ArrayList<>(TASKS);
    }
}
