package pl.allegro.tech.leaders.hackathon.challenge

import pl.allegro.tech.leaders.hackathon.challenge.base.InMemorySolutionClient
import spock.lang.Specification

import static pl.allegro.tech.leaders.hackathon.challenge.base.SampleChallenges.SAMPLE_CHALLENGES;

abstract class ChallengeSpec extends Specification {
    InMemorySolutionClient solutionClient = new InMemorySolutionClient()

    ChallengeFacade facade = new ChallengeConfiguration()
            .challengeService(SAMPLE_CHALLENGES)
}
