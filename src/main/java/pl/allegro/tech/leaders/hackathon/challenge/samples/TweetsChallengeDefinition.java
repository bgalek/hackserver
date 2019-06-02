package pl.allegro.tech.leaders.hackathon.challenge.samples;

import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import pl.allegro.tech.leaders.hackathon.challenge.ChallengeDefinition;
import pl.allegro.tech.leaders.hackathon.challenge.TaskDefinition;
import pl.allegro.tech.leaders.hackathon.challenge.TaskScoring;

import java.util.List;
import java.util.Map;

@Component
class TweetsChallengeDefinition implements ChallengeDefinition {

    @Override
    public String getName() {
        return "Twitter";
    }

    @Override
    public String getDescription() {
        return "Your task is to show me my tweets!";
    }

    @Override
    public String getChallengeEndpoint() {
        return "/tweets";
    }

    @Override
    public Class<String> solutionType() {
        return String.class;
    }

    @Override
    public List<QueryParam> getChallengeParams() {
        return List.of(new QueryParam("user", "user login"));
    }

    @Override
    public TaskDefinition getExample() {
        return TaskDefinition.withFixedResult("tweets", new LinkedMultiValueMap<>(Map.of("user", List.of("allegro.tech"))), "4",
                new TaskScoring(4, 0));
    }

    @Override
    public List<TaskDefinition> getTasks() {
        return List.of(getExample());
    }
}
