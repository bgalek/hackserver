package pl.allegro.experiments.chi.chiserver.infrastructure.interactions;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import javassist.bytecode.ByteArray;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.Metric;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import pl.allegro.experiments.chi.chiserver.domain.interactions.InteractionRepository;
import pl.allegro.tech.common.andamio.server.cloud.CloudMetadata;
import pl.allegro.tech.common.andamio.spring.avro.AvroConverter;

import java.util.*;

@Configuration
public class KafkaConfig {
    private final List<String> metricsList = Arrays.asList("request-rate", "record-send-rate", "request-latency-avg", "request-latency-max", "record-error-rate");

    @Bean
    @ConditionalOnProperty(name = {"interactions.repository"}, havingValue = "local")
    public InteractionRepository localInteractionRepository() {
        return new LoggerInteractionRepository();
    }

    @Bean
    @ConditionalOnProperty(name = {"interactions.repository"}, havingValue = "kafka")
    public InteractionRepository kafkaInteractionRepository(
            KafkaTemplate<String, byte[]> kafkaTemplate,
            AvroConverter avroConverter,
            @Value("${interactions.kafka.topic}") String kafkaTopic) {
        return new KafkaInteractionRepository(kafkaTemplate, avroConverter, kafkaTopic);
    }

    @Bean
    @ConditionalOnProperty(name = {"interactions.repository"}, havingValue = "kafka")
    public KafkaTemplate kafkaTemplate(
            @Value("${interactions.kafka.bootstrap-servers-dc4}") String bootstrapServersDc4,
            @Value("${interactions.kafka.bootstrap-servers-dc5}") String bootstrapServersDc5,
            CloudMetadata cloudMetadata,
            @Value("${interactions.kafka.batch-size}") int batchSize,
            @Value("${interactions.kafka.linger-ms}") int lingerMs,
            MetricRegistry metricRegistry) {
        String bootstrapServer = cloudMetadata.getDatacenter().equals("dc5") ? bootstrapServersDc5 : bootstrapServersDc4;
        KafkaTemplate<String, ByteArray> kafkaTemplate = new KafkaTemplate<String, ByteArray>(producerFactory(bootstrapServer, batchSize, lingerMs));
        metricsList.forEach(metricName ->
                metricRegistry.register("kafka." + metricName, new Gauge<Double>() {
                    @Override
                    public Double getValue() {
                        return getMetricValue(kafkaTemplate, metricName);
                    }
                })
        );

        return kafkaTemplate;
    }

    private double getMetricValue(KafkaTemplate<String, ByteArray> kafkaTemplate, String metricName) {
        return kafkaTemplate.metrics().keySet().stream()
                .filter(it -> it.name().equals(metricName) && it.tags().size() < 2)
                .findFirst()
                .flatMap(it -> {
                    Metric metric = kafkaTemplate.metrics().get(it);
                    return Optional.ofNullable(metric != null ? metric.value() : null);
                })
                .orElse(.0);
    }

    private DefaultKafkaProducerFactory<String, ByteArray> producerFactory(
            String bootstrapServers,
            int batchSize,
            int lingerMs) {
        return new DefaultKafkaProducerFactory<String, ByteArray>(config(bootstrapServers, batchSize, lingerMs));
    }

    private Map<String, Object> config(
            String bootstrapServers,
            int batchSize,
            int lingerMs) {
        Map<String, Object> result = new HashMap<>();
        result.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        result.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class);
        result.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        result.put(ProducerConfig.BATCH_SIZE_CONFIG, batchSize);
        result.put(ProducerConfig.LINGER_MS_CONFIG, lingerMs);
        result.put(ProducerConfig.ACKS_CONFIG, "1");
        result.put(ProducerConfig.RETRIES_CONFIG, "5");
        result.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, "1000");
        return result;
    }
}
