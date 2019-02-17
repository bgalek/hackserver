package pl.allegro.tech.leaders.hackathon.challenge.base

import groovy.transform.CompileStatic
import pl.allegro.tech.leaders.hackathon.challenge.ChallengeDefinition
import pl.allegro.tech.leaders.hackathon.challenge.ChallengeTaskDefinition

@CompileStatic
class SumChallengeDefinition implements ChallengeDefinition<String> {
    public static final ID = "sum"
    public static final ChallengeTaskDefinition FIRST_TASK = ChallengeTaskDefinition.withFixedResult("Should sum 2 numbers to 5", [equation: "2+3"], "5", 1)
    public static final ChallengeTaskDefinition SECOND_TASK = ChallengeTaskDefinition.withFixedResult("Should sum 3 numbers to 9", [equation: "2+3+4"], "9", 1)
    public static final ChallengeTaskDefinition THIRD_TASK = ChallengeTaskDefinition.withFixedResult("Should sum 4 numbers to 14", [equation: "2+3+4+5"], "14", 1)

    @Override
    String getId() {
        ID
    }

    @Override
    String getName() {
        "Sum Challenge"
    }

    @Override
    String getDescription() {
        "Your task is to make sum calculator api!"
    }

    @Override
    String getChallengeEndpoint() {
        "/sum"
    }

    @Override
    List<QueryParam> getChallengeParams() {
        [new QueryParam("query", "equation to samples")]
    }

    @Override
    List<ChallengeTaskDefinition> getTasks() {
        [FIRST_TASK, SECOND_TASK, THIRD_TASK]
    }

    @Override
    Class<String> solutionType() {
        String
    }
}