package pl.allegro.tech.leaders.hackathon.registration.infrastructure;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Configuration
class RegistrationEventPublisherConfiguration {

    @Bean
    Executor executor() {
        return Executors.newSingleThreadExecutor();
    }

    @Bean
    RegistrationEventPublisher registrationEventPublisher(Executor executor) {
        return new RegistrationEventPublisher(executor);
    }
}

