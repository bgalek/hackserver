package pl.allegro.tech.leaders.hackathon.challenge.api;

import pl.allegro.tech.leaders.hackathon.infrastructure.events.DomainEvent;

public class ChallengeUpdatedEvent extends DomainEvent {

    private static final String EVENT_TYPE = "CHALLENGES_UPDATED";

    public ChallengeUpdatedEvent(ChallengeDetails challengeDetails) {
        super(EVENT_TYPE, challengeDetails);
    }
}
