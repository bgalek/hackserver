package pl.allegro.tech.leaders.hackathon.challenge.base

import pl.allegro.tech.leaders.hackathon.challenge.api.ChallengeDefinition
import pl.allegro.tech.leaders.hackathon.challenge.api.ChallengeDetails

import java.time.Instant

import static java.util.Objects.requireNonNull

class ChallengeDetailsAssertions {
    static ChallengeDetailsAssertions expectChallengeDetails(ChallengeDetails challengeDetails) {
        return new ChallengeDetailsAssertions(challengeDetails)
    }

    private final ChallengeDetails challengeDetails

    ChallengeDetailsAssertions(ChallengeDetails challengeDetails) {
        this.challengeDetails = requireNonNull(challengeDetails)
    }

    ChallengeDetailsAssertions toBeDerivedFrom(ChallengeDefinition definition) {
        assert challengeDetails.id == definition.id
        assert challengeDetails.name == definition.name
        assert challengeDetails.description == definition.description
        assert challengeDetails.challengeEndpoint == definition.challengeEndpoint
        assert challengeDetails.challengeParams == definition.challengeParams
        assert challengeDetails.challengeResponse == definition.challengeResponse
        assert challengeDetails.examples == definition.examples
        return this
    }

    ChallengeDetailsAssertions toBeActivatedAt(Instant activatedAt) {
        assert challengeDetails.active
        assert challengeDetails.activatedAt == activatedAt
        return this
    }
}
