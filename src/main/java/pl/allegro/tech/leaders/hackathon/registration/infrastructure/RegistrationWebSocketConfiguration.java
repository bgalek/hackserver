package pl.allegro.tech.leaders.hackathon.registration.infrastructure;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import pl.allegro.tech.leaders.hackathon.registration.events.TeamRegisteredEvent;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.util.Collections;
import java.util.function.Consumer;

@Configuration
class RegistrationWebSocketConfiguration {

    @Bean
    HandlerMapping handlerMapping(WebSocketHandler wsh) {
        return new SimpleUrlHandlerMapping() {
            {
                setUrlMap(Collections.singletonMap("/ws/registrations", wsh));
            }
        };
    }

    @Bean
    WebSocketHandler webSocketHandler(ObjectMapper objectMapper, Consumer<FluxSink<TeamRegisteredEvent>> registrationEventPublisher) {
        Flux<TeamRegisteredEvent> publish = Flux.create(registrationEventPublisher).share();
        return session -> {
            Flux<WebSocketMessage> messageFlux = publish
                    .map(evt -> {
                        try {
                            return objectMapper.writeValueAsString(evt.getSource());
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .map(session::textMessage);
            return session.send(messageFlux);
        };
    }
}