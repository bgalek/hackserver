package pl.allegro.tech.leaders.hackathon.challenge;

import pl.allegro.tech.leaders.hackathon.challenge.Challenge.ChallengeState;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

class ChallengeCreator {
    private final Map<String, ChallengeDefinition> definitions = new HashMap<>();

    Challenge createChallenge(ChallengeDefinition challengeDefinition) {
        ChallengeDefinition cachedDefinition = definitions.get(challengeDefinition.getId());
        if (cachedDefinition != null && !cachedDefinition.equals(challengeDefinition)) {
            throw new IllegalArgumentException("Duplicated challenge definition id: " + challengeDefinition.getId());
        }
        definitions.put(challengeDefinition.getId(), challengeDefinition);
        return new Challenge(challengeDefinition);
    }

    Challenge restoreChallenge(ChallengeState challengeState) {
        ChallengeDefinition definition = getDefinitionOrThrow(challengeState.id);
        return new Challenge(challengeState, definition);
    }

    private ChallengeDefinition getDefinitionOrThrow(String challengeId) {
        return Optional.ofNullable(definitions.get(challengeId))
                .orElseThrow(() -> new IllegalStateException("Could not find challenge definition with id: " + challengeId));
    }
}
