package pl.allegro.experiments.chi.chiserver.interactions.infrastructure

import com.codahale.metrics.MetricRegistry
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.KafkaTemplate
import pl.allegro.tech.common.andamio.spring.avro.AvroConverter

@Configuration
class BufferedKafkaInteractionRepositoryConfig {

    @Bean("bufferedKafkaInteractionRepository")
    @ConditionalOnProperty(name = arrayOf("interactions.repository"), havingValue = "kafka")
    fun bufferedKafkaInteractionRepository(
            buffer: InteractionBuffer,
            metricRegistry: MetricRegistry,

            kafkaTemplate: KafkaTemplate<String, ByteArray>,
            avroConverter: AvroConverter,
            @Value("\${interactions.kafka.topic}") topic: String,
            @Value("\${interactions.kafka.send-timeout}") sendTimeout: Long,
            @Value("\${interactions.repository}") repositoryType: String

    ): BufferedInteractionRepository {
        return BufferedInteractionRepository(metricRegistry, buffer, KafkaInteractionRepository(kafkaTemplate, avroConverter, topic, sendTimeout))
    }

    @Bean
    @ConditionalOnMissingBean(name = arrayOf("bufferedKafkaInteractionRepository"))
    fun loggerInteractionRepository(
            buffer: InteractionBuffer,
            metricRegistry: MetricRegistry): BufferedInteractionRepository {
        return BufferedInteractionRepository(metricRegistry, buffer, LoggerInteractionRepository())
    }

    @Bean
    fun buffer(
            @Value("\${interactions.buffer-size}") size: Int,
            metricRegistry: MetricRegistry): InteractionBuffer {
        return InteractionBuffer(size, metricRegistry)
    }
}