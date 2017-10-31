package pl.allegro.experiments.chi.chiserver

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import pl.allegro.experiments.chi.chiserver.assignments.infrastructure.InMemoryExperimentAssignmentRepository

@Configuration
class AssignmentsIntegrationTestConfig {
    @Primary
    @Bean
    InMemoryExperimentAssignmentRepository experimentAssignmentRepository() {
        return new InMemoryExperimentAssignmentRepository()
    }
}
