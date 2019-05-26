package pl.allegro.tech.leaders.hackathon.infrastructure.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;

import java.util.Collections;

@Configuration
class WebSocketConfiguration {

    @Bean
    WebSocketConnectionsHandler webSocketHandler(ObjectMapper objectMapper) {
        return new WebSocketConnectionsHandler(objectMapper);
    }

    @Bean
    HandlerMapping handlerMapping(WebSocketConnectionsHandler webSocketConnectionsHandler) {
        return new SimpleUrlHandlerMapping() {
            {
                setUrlMap(Collections.singletonMap("/ws/events", webSocketConnectionsHandler));
                setOrder(10);
            }
        };
    }

    @Bean
    WebSocketHandlerAdapter handlerAdapter() {
        return new WebSocketHandlerAdapter();
    }
}