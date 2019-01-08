package pl.allegro.tech.leaders.hackathon.challenge

import pl.allegro.tech.leaders.hackathon.challenge.api.SolutionClient
import spock.lang.Specification

import static pl.allegro.tech.leaders.hackathon.challenge.base.SampleChallenges.SAMPLE_CHALLENGES;

abstract class ChallengeSpec extends Specification {
    SolutionClient solutionClient = Mock()

    ChallengeFacade facade = new ChallengeConfiguration()
            .challengeService(SAMPLE_CHALLENGES)
}
