package pl.allegro.tech.leaders.hackathon.challenge.base


import groovy.transform.CompileStatic
import pl.allegro.tech.leaders.hackathon.challenge.ChallengeDefinition
import pl.allegro.tech.leaders.hackathon.challenge.ChallengeTaskDefinition

@CompileStatic
class CountChallengeDefinition implements ChallengeDefinition<String> {
    public static final ID = "count"

    @Override
    String getId() {
        ID
    }

    @Override
    String getName() {
        "Count string occurrences"
    }

    @Override
    String getDescription() {
        "Your task is to count string occurrences!"
    }

    @Override
    String getChallengeEndpoint() {
        "/count"
    }

    @Override
    List<QueryParam> getChallengeParams() {
        [
                new QueryParam("text", "sample text to check"),
                new QueryParam("phrase", "sample text to count in the text")
        ]
    }

    @Override
    List<ChallengeTaskDefinition<String>> getTasks() {
        [
                ChallengeTaskDefinition.withFixedResult("Should find two occurrences", [text: "abba", token: "b"], "2", 1),
                ChallengeTaskDefinition.withFixedResult("Should find one occurrence", [text: "alphabet", token: "b"], "1", 1)
        ]
    }

    @Override
    Class<String> solutionType() {
        String
    }
}