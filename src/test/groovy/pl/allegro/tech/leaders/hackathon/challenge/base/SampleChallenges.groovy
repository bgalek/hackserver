package pl.allegro.tech.leaders.hackathon.challenge.base

import groovy.transform.CompileStatic
import pl.allegro.tech.leaders.hackathon.challenge.api.ChallengeDefinition

@CompileStatic
class SampleChallenges {
    public static final ChallengeDefinition SUM_CHALLENGE = new SumChallengeDefinition()
    public static final ChallengeDefinition COUNT_CHALLENGE = new CountChallengeDefinition()
    public static final ChallengeDefinition SAMPLE_CHALLENGE = SUM_CHALLENGE
}
