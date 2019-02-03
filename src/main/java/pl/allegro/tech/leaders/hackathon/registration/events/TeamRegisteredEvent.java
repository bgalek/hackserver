package pl.allegro.tech.leaders.hackathon.registration.events;

import org.springframework.context.ApplicationEvent;
import pl.allegro.tech.leaders.hackathon.registration.api.RegisteredTeam;

public class TeamRegisteredEvent extends ApplicationEvent {

    public TeamRegisteredEvent(RegisteredTeam registeredTeam) {
        super(new Action("TEAM_REGISTERED", registeredTeam));
    }

    private static class Action {
        private final String type;
        private final Object payload;

        Action(String type, Object payload) {
            this.type = type;
            this.payload = payload;
        }

        public String getType() {
            return type;
        }

        public Object getPayload() {
            return payload;
        }
    }
}
