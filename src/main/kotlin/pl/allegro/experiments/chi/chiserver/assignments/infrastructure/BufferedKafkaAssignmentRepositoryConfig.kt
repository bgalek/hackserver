package pl.allegro.experiments.chi.chiserver.assignments.infrastructure

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.KafkaTemplate
import pl.allegro.tech.common.andamio.spring.avro.AvroConverter

@Configuration
class BufferedKafkaAssignmentRepositoryConfig {

    @Bean
    fun bufferedKafkaAssignmentRepository(
            buffer: AssignmentBuffer,
            metricReporter: MetricReporter,

            kafkaTemplate: KafkaTemplate<String, ByteArray>,
            avroConverter: AvroConverter,
            @Value("\${assignments.kafka.topic}") topic: String,
            @Value("\${assignments.kafka.send-timeout}") sendTimeout: Long

    ): BufferedAssignmentRepository {
        return BufferedAssignmentRepository(metricReporter, buffer, KafkaAssignmentRepository(kafkaTemplate, avroConverter, topic, sendTimeout))
    }

    @Bean
    fun buffer(@Value("\${assignments.buffer-size}") size: Int): AssignmentBuffer {
        return AssignmentBuffer(size)
    }
}