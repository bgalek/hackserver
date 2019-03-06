package pl.allegro.tech.leaders.hackathon.challenge.base

import groovy.transform.CompileStatic
import pl.allegro.tech.leaders.hackathon.challenge.ChallengeDefinition

@CompileStatic
class SampleChallenges {
    public static final SumChallengeDefinition SUM_CHALLENGE = new SumChallengeDefinition()
    public static final CountChallengeDefinition COUNT_CHALLENGE = new CountChallengeDefinition()
    public static final CountChallengeDefinition SAMPLE_CHALLENGE = COUNT_CHALLENGE
    public static final List<ChallengeDefinition> SAMPLE_CHALLENGES = [SUM_CHALLENGE, COUNT_CHALLENGE] as List<ChallengeDefinition>
}
