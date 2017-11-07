package pl.allegro.experiments.chi.chiserver.interactions.infrastructure

import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.ByteArraySerializer
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory
import pl.allegro.experiments.chi.chiserver.interactions.InteractionRepository
import pl.allegro.tech.common.andamio.server.cloud.CloudMetadata
import pl.allegro.tech.common.andamio.spring.avro.AvroConverter

@Configuration
class KafkaConfig {

    @Bean
    @ConditionalOnProperty(name = arrayOf("interactions.repository"), havingValue = "local")
    fun localInteractionRepository(): InteractionRepository {
        return LoggerInteractionRepository()
    }

    @Bean
    @ConditionalOnProperty(name = arrayOf("interactions.repository"), havingValue = "kafka")
    fun kafkaInteractionRepository(
            kafkaTemplate: KafkaTemplate<String, ByteArray>,
            avroConverter: AvroConverter,
            @Value("\${interactions.kafka.topic}") kafkaTopic: String): InteractionRepository {
        return KafkaInteractionRepository(kafkaTemplate, avroConverter, kafkaTopic)
    }

    @Bean
    @ConditionalOnProperty(name = arrayOf("interactions.repository"), havingValue = "kafka")
    fun kafkaTemplate(
            @Value("\${interactions.kafka.bootstrap-servers-dc4}") bootstrapServersDc4: String,
            @Value("\${interactions.kafka.bootstrap-servers-dc5}") bootstrapServersDc5: String,
            cloudMetadata: CloudMetadata,
            @Value("\${interactions.kafka.batch-size}") batchSize: Int,
            @Value("\${interactions.kafka.linger-ms}") lingerMs: Int
            ): KafkaTemplate<String, ByteArray> {
        if (cloudMetadata.datacenter == "dc5") {
            return KafkaTemplate<String, ByteArray>(producerFactory(bootstrapServersDc5, batchSize, lingerMs))
        } else { // dc4 or localhost
            return KafkaTemplate<String, ByteArray>(producerFactory(bootstrapServersDc4, batchSize, lingerMs))
        }
    }

    private fun producerFactory(
            bootstrapServers: String,
            batchSize: Int,
            lingerMs: Int): ProducerFactory<String, ByteArray> =
            DefaultKafkaProducerFactory<String, ByteArray>(config(bootstrapServers, batchSize, lingerMs))

    private fun config(
            bootstrapServers: String,
            batchSize: Int,
            lingerMs: Int) =
            mapOf(
                    ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
                    ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to ByteArraySerializer::class.java,
                    ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapServers,
                    ProducerConfig.BATCH_SIZE_CONFIG to batchSize,
                    ProducerConfig.LINGER_MS_CONFIG to lingerMs
            )
}