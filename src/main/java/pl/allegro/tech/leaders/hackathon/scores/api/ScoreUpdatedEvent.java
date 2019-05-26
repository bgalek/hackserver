package pl.allegro.tech.leaders.hackathon.scores.api;

import pl.allegro.tech.leaders.hackathon.infrastructure.events.DomainEvent;

public class ScoreUpdatedEvent extends DomainEvent {

    private static final String EVENT_TYPE = "SCORES_UPDATED";

    public ScoreUpdatedEvent(HackatonScores hackatonScores) {
        super(EVENT_TYPE, hackatonScores);
    }
}