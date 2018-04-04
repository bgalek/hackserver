package pl.allegro.experiments.chi.chiserver.infrastructure.experiments.converters;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConvertersConfig {
    @Bean
    DateTimeDeserializer dateTimeDeserializer() {
        return new DateTimeDeserializer();
    }

    @Bean
    DateTimeSerializer dateTimeSerializer() {
        return new DateTimeSerializer();
    }

    @Bean
    ActivityPeriodDeserializer activityPeriodDeserializer(DateTimeDeserializer dateTimeDeserializer) {
        return new ActivityPeriodDeserializer(dateTimeDeserializer);
    }

    @Bean
    ActivityPeriodSerializer activityPeriodSerializer(DateTimeSerializer dateTimeSerializer) {
        return new ActivityPeriodSerializer(dateTimeSerializer);
    }

    @Bean
    PredicateSerializer predicateSerializer() {
        return new PredicateSerializer();
    }

    @Bean
    PredicateDeserializer predicateDeserializer() {
        return new PredicateDeserializer();
    }

    @Bean
    ExperimentVariantSerializer experimentVariantSerializer(PredicateSerializer predicateSerializer) {
        return new ExperimentVariantSerializer(predicateSerializer);
    }

    @Bean
    ExperimentSerializer experimentSerializer(DateTimeSerializer dateTimeSerializer) {
        return new ExperimentSerializer(dateTimeSerializer);
    }

    @Bean
    ExperimentDeserializer experimentDeserializer(
            ActivityPeriodDeserializer activityPeriodDeserializer) {
        return new ExperimentDeserializer(activityPeriodDeserializer);
    }
}
