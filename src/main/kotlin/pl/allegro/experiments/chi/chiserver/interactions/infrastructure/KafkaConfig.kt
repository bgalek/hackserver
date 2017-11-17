package pl.allegro.experiments.chi.chiserver.interactions.infrastructure

import com.codahale.metrics.Gauge
import com.codahale.metrics.MetricRegistry
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.MetricName
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
import java.util.*

@Configuration
class KafkaConfig {
    private val metricsList = listOf("request-rate", "record-send-rate", "request-latency-avg", "request-latency-max", "record-error-rate")

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
            @Value("\${interactions.kafka.linger-ms}") lingerMs: Int,
            metricRegistry : MetricRegistry
    ): KafkaTemplate<String, ByteArray> {

        val bootstrapServer = if (cloudMetadata.datacenter == "dc5") {
            bootstrapServersDc5
        } else { // dc4 or localhost
            bootstrapServersDc4
        }

        val kafkaTemplate =  KafkaTemplate<String, ByteArray>(producerFactory(bootstrapServer, batchSize, lingerMs))

        metricsList.forEach { metricName: String ->
            metricRegistry.register("kafka." + metricName, Gauge<Double> {
                getMetricValue(kafkaTemplate, metricName)
            })
        }

        return kafkaTemplate
    }

    private fun getMetricValue(kafkaTemplate : KafkaTemplate<String, ByteArray>, metricName : String) : Double {
        return kafkaTemplate.metrics().keys.stream()
                .filter {it.name() == metricName && it.tags().size < 2}.findFirst()
                .flatMap {Optional.ofNullable(kafkaTemplate.metrics()[it]?.value())}
                .orElse(.0)
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
                    ProducerConfig.LINGER_MS_CONFIG to lingerMs,
                    ProducerConfig.ACKS_CONFIG to "1",
                    ProducerConfig.RETRIES_CONFIG to "5",
                    ProducerConfig.RETRY_BACKOFF_MS_CONFIG to "1000"
            )
}
