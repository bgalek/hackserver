package pl.allegro.experiments.chi.chiserver.assignments.infrastructure

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

@Configuration
class BufferedKafkaAssignmentRepositoryConfig {
    @Bean
    @Primary
    fun bufferedKafkaAssignmentRepository(
            buffer: AssignmentBuffer,
            kafkaRepository: KafkaAssignmentRepository,
            metricReporter: MetricReporter): BufferedAssignmentRepository {
        return BufferedAssignmentRepository(metricReporter, buffer, kafkaRepository)
    }

    @Bean
    fun buffer(@Value("\${assignments.buffer-size}") size: Int): AssignmentBuffer {
        return AssignmentBuffer(size)
    }
}