package pl.allegro.tech.leaders.hackathon.challenge.samples;

import com.google.common.graph.GraphBuilder;
import com.google.common.graph.Graphs;
import com.google.common.graph.ImmutableGraph;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import pl.allegro.tech.leaders.hackathon.challenge.ChallengeDefinition;
import pl.allegro.tech.leaders.hackathon.challenge.TaskDefinition;
import pl.allegro.tech.leaders.hackathon.challenge.TaskScoring;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@SuppressWarnings("UnstableApiUsage")
public class GraphChallengeDefinition implements ChallengeDefinition {

    @Override
    public String getName() {
        return "graphs";
    }

    @Override
    public String getDescription() {
        return "description";
    }

    @Override
    public String getChallengeEndpoint() {
        return "/graphs";
    }

    @Override
    public List<QueryParam> getChallengeParams() {
        return List.of();
    }

    @Override
    public Class<?> solutionType() {
        return Integer.class;
    }

    @Override
    public TaskDefinition getExample() {
        return TaskDefinition.withFixedResult("example",
                new LinkedMultiValueMap<>(Map.of()),
                false,
                new TaskScoring(5, 1000));
    }

    @Override
    public List<TaskDefinition> getTasks() {

        Person rick = new Person("Rick");
        Person morty = new Person("Morty");
        Person jerry = new Person("Jerry");
        Person beth = new Person("Beth");
        Person summer = new Person("Summer");
        Person jessica = new Person("Jessica");
        Person mrMeeseeks = new Person("Mr. Meeseeks");
        Person snuffles = new Person("Snuffles");

        ImmutableGraph<Person> graph = GraphBuilder.undirected()
                .<Person>immutable()
                .addNode(rick)
                .addNode(morty)
                .addNode(jerry)
                .addNode(beth)
                .addNode(summer)
                .addNode(jessica)
                .addNode(mrMeeseeks)
                .addNode(snuffles)
                .putEdge(rick, morty)
                .putEdge(morty, summer)
                .putEdge(summer, rick)
                .build();

        return List.of(
                TaskDefinition.withFixedResult("has cycle?",
                        new LinkedMultiValueMap<>(
                                Map.of(
                                        "graph", graph.nodes().stream().map(Person::name).collect(Collectors.toList()),
                                        "edges", graph.edges().stream().map(it -> "%s->%s".formatted(it.nodeU().name(), it.nodeV().name())).collect(Collectors.toList())
                                )),
                        Graphs.hasCycle(graph),
                        new TaskScoring(10, 1000))
        );
    }

    public record Person(String name) {
    }
}
