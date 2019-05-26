package pl.allegro.tech.leaders.hackathon.infrastructure.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.ApplicationListener;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import pl.allegro.tech.leaders.hackathon.infrastructure.events.DomainEvent;
import reactor.core.publisher.DirectProcessor;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

class WebSocketConnectionsHandler implements WebSocketHandler, ApplicationListener<DomainEvent> {
    private final DirectProcessor<WebSocketSessionHandler> connectedProcessor;
    private final List<WebSocketSessionHandler> sessionList;
    private final ObjectMapper objectMapper;

    public WebSocketConnectionsHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.connectedProcessor = DirectProcessor.create();
        this.sessionList = new ArrayList<>();
    }

    @Override
    public Mono<Void> handle(WebSocketSession webSocketSession) {
        WebSocketSessionHandler sessionHandler = new WebSocketSessionHandler(objectMapper);
        sessionHandler.connected().subscribe(value -> sessionList.add(sessionHandler));
        sessionHandler.disconnected().subscribe(value -> sessionList.remove(sessionHandler));
        connectedProcessor.sink().next(sessionHandler);
        return sessionHandler.handle(webSocketSession);
    }

    @Override
    public void onApplicationEvent(DomainEvent domainEvent) {
        sessionList.forEach(session -> session.sendEvent(domainEvent));
    }
}