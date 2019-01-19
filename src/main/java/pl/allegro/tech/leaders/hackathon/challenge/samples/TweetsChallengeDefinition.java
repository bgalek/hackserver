package pl.allegro.tech.leaders.hackathon.challenge.samples;

import org.springframework.stereotype.Component;
import pl.allegro.tech.leaders.hackathon.challenge.ChallengeDefinition;
import pl.allegro.tech.leaders.hackathon.challenge.ChallengeTaskDefinition;

import java.util.List;

import static java.util.Collections.emptyList;

@Component
class TweetsChallengeDefinition implements ChallengeDefinition<String> {

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
    public List<ChallengeTaskDefinition<String>> getTasks() {
        return emptyList();
    }
}
