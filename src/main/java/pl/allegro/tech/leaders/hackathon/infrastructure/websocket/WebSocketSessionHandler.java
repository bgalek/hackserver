package pl.allegro.tech.leaders.hackathon.infrastructure.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.reactive.socket.WebSocketSession;
import pl.allegro.tech.leaders.hackathon.infrastructure.events.DomainEvent;
import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoProcessor;
import reactor.netty.channel.AbortedException;

import java.nio.channels.ClosedChannelException;

public class WebSocketSessionHandler {

    private final MonoProcessor<WebSocketSession> connectedProcessor;
    private final MonoProcessor<WebSocketSession> disconnectedProcessor;
    private final ObjectMapper objectMapper;

    private boolean webSocketConnected;
    private WebSocketSession webSocketSession;

    WebSocketSessionHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.connectedProcessor = MonoProcessor.create();
        this.disconnectedProcessor = MonoProcessor.create();
        this.webSocketConnected = false;
    }

    protected Mono<Void> handle(WebSocketSession webSocketSession) {
        this.webSocketSession = webSocketSession;
        Mono<WebSocketSession> connected = Mono.fromRunnable(() -> {
            webSocketConnected = true;
            connectedProcessor.onNext(webSocketSession);
        });
        Mono<WebSocketSession> disconnected = Mono.fromRunnable(() -> {
            webSocketConnected = false;
            disconnectedProcessor.onNext(webSocketSession);
        });
        return connected.thenMany(webSocketSession.receive())
                .then(disconnected)
                .then();
    }

    Mono<WebSocketSession> connected() {
        return connectedProcessor;
    }

    Mono<WebSocketSession> disconnected() {
        return disconnectedProcessor;
    }

    void sendEvent(DomainEvent event) {
        if (webSocketConnected) {
            webSocketSession
                    .send(Mono.just(webSocketSession.textMessage(toJson(event))))
                    .doOnError(ClosedChannelException.class, t -> closeConnection())
                    .doOnError(AbortedException.class, t -> closeConnection())
                    .onErrorResume(ClosedChannelException.class, t -> Mono.empty())
                    .onErrorResume(AbortedException.class, t -> Mono.empty())
                    .subscribe();
        }
    }

    private void closeConnection() {
        if (webSocketConnected) {
            webSocketConnected = false;
            disconnectedProcessor.onNext(webSocketSession);
        }
    }

    private String toJson(DomainEvent domainEvent) {
        try {
            return objectMapper.writeValueAsString(domainEvent);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}