package pl.allegro.experiments.chi.chiserver.interactions

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import pl.allegro.experiments.chi.chiserver.domain.interactions.InteractionRepository
import pl.allegro.experiments.chi.chiserver.infrastructure.InMemoryExperimentsRepository
import pl.allegro.experiments.chi.chiserver.infrastructure.interactions.InMemoryInteractionRepository

@Configuration
class InteractionsIntegrationTestConfig {
    
    @Primary
    @Bean
    InteractionRepository experimentInteractionRepository() {
        return new InMemoryInteractionRepository()
    }
}