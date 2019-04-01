package pl.allegro.tech.leaders.hackathon.challenge.samples;

import org.springframework.stereotype.Component;
import pl.allegro.tech.leaders.hackathon.challenge.ChallengeDefinition;
import pl.allegro.tech.leaders.hackathon.challenge.TaskDefinition;

import java.util.List;
import java.util.Map;

@Component
public class TweetsChallengeDefinition implements ChallengeDefinition {
    public static final String ID = "tweets";

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public String getName() {
        return "Twitter Challenge";
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
        return TaskDefinition.withFixedResult("tweets", Map.of("user", "allegro.tech"), "4", 4);
    }

    @Override
    public List<TaskDefinition> getTasks() {
        return List.of(getExample());
    }
}
