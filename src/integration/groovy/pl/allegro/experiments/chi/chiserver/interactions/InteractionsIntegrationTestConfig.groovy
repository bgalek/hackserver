package pl.allegro.experiments.chi.chiserver.interactions

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import pl.allegro.experiments.chi.chiserver.interactions.infrastructure.InMemoryInteractionRepository

@Configuration
class InteractionsIntegrationTestConfig {
    @Primary
    @Bean
    InMemoryInteractionRepository experimentInteractionRepository() {
        return new InMemoryInteractionRepository()
    }
}
