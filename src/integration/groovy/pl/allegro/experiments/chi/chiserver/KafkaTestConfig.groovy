package pl.allegro.experiments.chi.chiserver

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import pl.allegro.experiments.chi.chiserver.assignments.ExperimentAssignmentRepository
import pl.allegro.experiments.chi.chiserver.assignments.InMemoryExperimentAssignmentRepository
import pl.allegro.experiments.chi.chiserver.assignments.infrastructure.KafkaAssignmentRepository

@Configuration
class KafkaTestConfig {

    @Bean
    @Primary
    ExperimentAssignmentRepository inMemoryAssignmentRepository() {
        return new InMemoryExperimentAssignmentRepository()
    }
}
