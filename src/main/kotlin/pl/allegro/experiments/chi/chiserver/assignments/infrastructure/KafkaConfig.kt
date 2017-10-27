package pl.allegro.experiments.chi.chiserver.assignments.infrastructure

import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.ByteArraySerializer
import org.apache.kafka.common.serialization.BytesSerializer
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory
import pl.allegro.tech.common.andamio.spring.avro.AvroConverter

@Configuration
class KafkaConfig {

    @Bean
    fun kafkaEventEmitter(kafkaTemplate: KafkaTemplate<String, ByteArray>,
                          avroConverter: AvroConverter,
                          @Value("\${assignments.kafka.topic}") topic: String) =
            KafkaEventEmitter(kafkaTemplate, avroConverter, topic)

    @Bean
    fun kafkaTemplate(@Value("\${assignments.kafka.bootstrap-servers}") bootstrapServers: String): KafkaTemplate<String, ByteArray> =
            KafkaTemplate<String, ByteArray>(producerFactory(bootstrapServers))

    private fun producerFactory(bootstrapServers: String): ProducerFactory<String, ByteArray> =
            DefaultKafkaProducerFactory<String, ByteArray>(config(bootstrapServers))

    private fun config(bootstrapServers: String) =
            mapOf(
                    ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
                    ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to ByteArraySerializer::class.java,
                    ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapServers
            )
}