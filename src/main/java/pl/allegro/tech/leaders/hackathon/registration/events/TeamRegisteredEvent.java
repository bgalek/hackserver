package pl.allegro.tech.leaders.hackathon.registration.events;

import org.springframework.context.ApplicationEvent;
import pl.allegro.tech.leaders.hackathon.registration.api.RegisteredTeam;

public class TeamRegisteredEvent extends ApplicationEvent {

    public TeamRegisteredEvent(RegisteredTeam registeredTeam) {
        super(registeredTeam);
    }
}
