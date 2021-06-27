package pl.allegro.tech.leaders.hackathon.challenge.samples;

import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import pl.allegro.tech.leaders.hackathon.challenge.ChallengeDefinition;
import pl.allegro.tech.leaders.hackathon.challenge.TaskDefinition;
import pl.allegro.tech.leaders.hackathon.challenge.TaskScoring;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
public class GraphChallengeDefinition implements ChallengeDefinition {

    @Override
    public String getName() {
        return "Longest cycle";
    }

    @Override
    public String getDescription() {
        return """
                    Let's draw some graphs!
                    Your task is to find the size of the longest cycle in the graph.
                    'A->B,C' means that you can travel from A to B and C (there are directed edges from
                    node A to node B, and from node A to node C).
                    Example:
                    Given graph 'A->B;B->A' longest available cycle goes from A to B and back to A. " +
                    It's size equals `2` (you can walk through 2 uniq nodes before visiting one you have seen before)
                """;
    }

    @Override
    public String getChallengeEndpoint() {
        return "/cycles";
    }

    @Override
    public List<QueryParam> getChallengeParams() {
        return List.of(
                new QueryParam("graph", "Graph definition. Each node as a separate url parameter.")
        );
    }

    @Override
    public Class<?> solutionType() {
        return Integer.class;
    }

    @Override
    public TaskDefinition getExample() {
        return TaskDefinition.withFixedResult("Should calculate cycle size of simple graph",
                new LinkedMultiValueMap<>(Map.of("graph", List.of("A->B", "B->A"))),
                2,
                new TaskScoring(4, 100),
                false
        );
    }

    @Override
    public List<TaskDefinition> getTasks() {
        return List.of(
                TaskDefinition.withFixedResult("Should calculate very small cycle size",
                        parameter("graph", "A->A"),
                        1, // A->A
                        new TaskScoring(5, 1000),
                        false
                ),
                TaskDefinition.withFixedResult("Should calculate cycle size of simple graph",
                        parameter("graph", "A->B,C", "B->A"),
                        2, // A->B->A
                        new TaskScoring(10, 1000),
                        false
                ),
                TaskDefinition.withFixedResult("Should calculate bigger cycle size",
                        parameter("graph", "A->A,B,C", "B->B,C,D", "C->C,D,E", "D->D,E,F", "E->E,F,A", "F->F,A,B"),
                        6, // A->B->C->D->E->F->A
                        new TaskScoring(20, 1000),
                        false
                ),
                TaskDefinition.withFixedResult("Should calculate longest cycle size",
                        parameter("graph", "A->B", "B->A,C", "C->A,B,C,D,E", "D->A,C,E", "E->C"),
                        4, // A->B->C->D->A
                        new TaskScoring(25, 1000),
                        false
                ),
                TaskDefinition.withFixedResult("It's getting complicated",
                        parameter("graph", "A->B,C", "B->C,D", "C->B,D,E", "D->B,C", "E->C,D,F"),
                        4, // B->C->E->D->B
                        new TaskScoring(30, 1000),
                        false
                ),
                TaskDefinition.withFixedResult("What do we have here?",
                        parameter("graph", "A->B,D", "B->D,F", "C->A,B,E,F", "D->F", "E->B,F"),
                        0, // B->C->E->D->B
                        new TaskScoring(30, 1000),
                        false
                )
        );
    }

    private MultiValueMap<String, String> parameter(String paramName, String... values) {
        return new LinkedMultiValueMap<>(Map.of(paramName, Arrays.stream(values).toList()));
    }
}
