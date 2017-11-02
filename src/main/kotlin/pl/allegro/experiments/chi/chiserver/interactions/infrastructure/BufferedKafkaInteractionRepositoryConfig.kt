package pl.allegro.experiments.chi.chiserver.interactions.infrastructure

import com.codahale.metrics.MetricRegistry
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.KafkaTemplate
import pl.allegro.experiments.chi.chiserver.interactions.InteractionRepository
import pl.allegro.tech.common.andamio.spring.avro.AvroConverter

@Configuration
class BufferedKafkaInteractionRepositoryConfig {

    @Bean
    fun bufferedKafkaAssignmentRepository(
            buffer: InteractionBuffer,
            metricRegistry: MetricRegistry,

            kafkaTemplate: KafkaTemplate<String, ByteArray>,
            avroConverter: AvroConverter,
            @Value("\${interactions.kafka.topic}") topic: String,
            @Value("\${interactions.kafka.send-timeout}") sendTimeout: Long,
            @Value("\${interactions.repository}") repositoryType: String

    ): BufferedInteractionRepository {
        val repo: InteractionRepository = if (repositoryType == "kafka") KafkaInteractionRepository(kafkaTemplate, avroConverter, topic, sendTimeout)
                else LoggerInteractionRepository()
        return BufferedInteractionRepository(metricRegistry, buffer, repo)
    }

    @Bean
    fun buffer(
            @Value("\${interactions.buffer-size}") size: Int,
            metricRegistry: MetricRegistry): InteractionBuffer {
        return InteractionBuffer(size, metricRegistry)
    }
}