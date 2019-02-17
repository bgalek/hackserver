package pl.allegro.tech.leaders.hackathon.challenge.samples;

import org.springframework.stereotype.Component;
import pl.allegro.tech.leaders.hackathon.challenge.ChallengeDefinition;
import pl.allegro.tech.leaders.hackathon.challenge.ChallengeTaskDefinition;

import java.util.List;
import java.util.Map;

@Component
class CalcChallengeDefinition implements ChallengeDefinition<String> {
    private final List<ChallengeTaskDefinition<String>> tasks = List.of(
            ChallengeTaskDefinition.withFixedResult("Should calculate a sum 2+2=4", Map.of("equation", "2+2"), "4", 1),
            ChallengeTaskDefinition.withFixedResult("Should calculate a sum and multiplication 2+2*2=6", Map.of("equation", "2+2*2"), "6", 1),
            ChallengeTaskDefinition.withFixedResult("Should respect parenthesis in equation (2+2)*2 = 8", Map.of("equation", "(2+2)*2"), "8", 1)
    );

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
    public List<ChallengeTaskDefinition<String>> getTasks() {
        return this.tasks;
    }
}
