package pl.allegro.experiments.chi.chiserver.assignments.infrastructure

import com.codahale.metrics.MetricRegistry
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.KafkaTemplate
import pl.allegro.experiments.chi.chiserver.assignments.AssignmentRepository
import pl.allegro.tech.common.andamio.spring.avro.AvroConverter

@Configuration
class BufferedKafkaAssignmentRepositoryConfig {

    @Bean
    fun bufferedKafkaAssignmentRepository(
            buffer: AssignmentBuffer,
            metricRegistry: MetricRegistry,

            kafkaTemplate: KafkaTemplate<String, ByteArray>,
            avroConverter: AvroConverter,
            @Value("\${assignments.kafka.topic}") topic: String,
            @Value("\${assignments.kafka.send-timeout}") sendTimeout: Long,
            @Value("\${assignments.repository}") repositoryType: String

    ): BufferedAssignmentRepository {
        val repo: AssignmentRepository = if (repositoryType == "kafka") KafkaAssignmentRepository(kafkaTemplate, avroConverter, topic, sendTimeout)
                else LoggerAssignmentRepository()
        return BufferedAssignmentRepository(metricRegistry, buffer, repo)
    }

    @Bean
    fun buffer(@Value("\${assignments.buffer-size}") size: Int): AssignmentBuffer {
        return AssignmentBuffer(size)
    }
}