package pl.allegro.tech.leaders.hackathon.challenge.base

import groovy.transform.CompileStatic
import pl.allegro.tech.leaders.hackathon.challenge.api.Challenge

@CompileStatic
class SampleChallenges {
    public static final Challenge SUM_CHALLENGE = new SumChallenge()
    public static final Challenge COUNT_CHALLENGE = new CountChallenge()
    public static final Challenge SAMPLE_CHALLENGE = SUM_CHALLENGE
    public static final List<Challenge> SAMPLE_CHALLENGES = [SUM_CHALLENGE, COUNT_CHALLENGE]
}
