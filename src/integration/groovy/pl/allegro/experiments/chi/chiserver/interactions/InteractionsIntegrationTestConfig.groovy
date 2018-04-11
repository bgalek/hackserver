package pl.allegro.experiments.chi.chiserver.interactions

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import pl.allegro.experiments.chi.chiserver.infrastructure.InMemoryExperimentsRepository
import pl.allegro.experiments.chi.chiserver.infrastructure.interactions.InMemoryInteractionRepository
import pl.allegro.experiments.chi.chiserver.utils.SampleInMemoryExperimentsRepository

@Configuration
class InteractionsIntegrationTestConfig {
    
    @Primary
    @Bean
    InMemoryInteractionRepository experimentInteractionRepository() {
        return new InMemoryInteractionRepository()
    }

    @Primary
    @Bean
    InMemoryExperimentsRepository experimentsRepository() {
        return SampleInMemoryExperimentsRepository.createSampleRepository()
    }
}
