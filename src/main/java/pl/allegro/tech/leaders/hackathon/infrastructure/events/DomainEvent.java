package pl.allegro.tech.leaders.hackathon.infrastructure.events;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.context.ApplicationEvent;

@JsonIgnoreProperties({"source"})
public class DomainEvent extends ApplicationEvent {

    private final String type;

    public DomainEvent(String type, Object payload) {
        super(payload);
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public Object getPayload() {
        return source;
    }
}
