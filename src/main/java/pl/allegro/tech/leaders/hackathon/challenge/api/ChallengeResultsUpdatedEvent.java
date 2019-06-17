package pl.allegro.tech.leaders.hackathon.challenge.api;

import pl.allegro.tech.leaders.hackathon.infrastructure.events.DomainEvent;

public class ChallengeResultsUpdatedEvent extends DomainEvent {

    private static final String EVENT_TYPE = "CHALLENGES_RESULTS_UPDATED";

    public ChallengeResultsUpdatedEvent(TaskResult taskResult) {
        super(EVENT_TYPE, taskResult);
    }
}