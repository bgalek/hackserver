package pl.allegro.experiments.chi.chiserver.interactions.infrastructure

import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.ByteArraySerializer
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory
import pl.allegro.tech.common.andamio.server.cloud.CloudMetadata

@Configuration
class KafkaConfig {

    @Bean
    fun kafkaTemplate(
            @Value("\${interactions.kafka.bootstrap-servers-dc4}") bootstrapServersDc4: String,
            @Value("\${interactions.kafka.bootstrap-servers-dc5}") bootstrapServersDc5: String,
            cloudMetadata: CloudMetadata
    ): KafkaTemplate<String, ByteArray> {
        if (cloudMetadata.datacenter == "dc5") {
            return KafkaTemplate<String, ByteArray>(producerFactory(bootstrapServersDc5))
        } else { // dc4 or localhost
            return KafkaTemplate<String, ByteArray>(producerFactory(bootstrapServersDc4))
        }
    }

    private fun producerFactory(bootstrapServers: String): ProducerFactory<String, ByteArray> =
            DefaultKafkaProducerFactory<String, ByteArray>(config(bootstrapServers))

    private fun config(bootstrapServers: String) =
            mapOf(
                    ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
                    ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to ByteArraySerializer::class.java,
                    ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapServers
            )
}