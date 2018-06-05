package pl.allegro.experiments.chi.chiserver.infrastructure.experiments.converters;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ReportingDefinition;

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
    ExperimentSerializer experimentSerializer(
            DateTimeSerializer dateTimeSerializer,
            ReportingDefinitionSerializer reportingDefinitionSerializer) {
        return new ExperimentSerializer(dateTimeSerializer, reportingDefinitionSerializer);
    }

    @Bean
    ExperimentDeserializer experimentDeserializer(
            ActivityPeriodDeserializer activityPeriodDeserializer,
            ReportingDefinitionDeserializer reportingDefinitionDeserializer) {
        return new ExperimentDeserializer(activityPeriodDeserializer, reportingDefinitionDeserializer);
    }

    @Bean
    EventDefinitionDeserializer eventDefinitionDeserializer() {
        return new EventDefinitionDeserializer();
    }

    @Bean
    EventDefinitionSerializer eventDefinitionSerializer() {
        return new EventDefinitionSerializer();
    }

    @Bean
    ReportingDefinitionSerializer reportingDefinitionSerializer(EventDefinitionSerializer eventDefinitionSerializer) {
        return new ReportingDefinitionSerializer(eventDefinitionSerializer);
    }

    @Bean
    ReportingDefinitionDeserializer reportingDefinitionDeserializer(EventDefinitionDeserializer eventDefinitionDeserializer) {
        return new ReportingDefinitionDeserializer(eventDefinitionDeserializer);
    }

    @Bean
    ExperimentGroupDeserializer experimentGroupDeserializer() {
        return new ExperimentGroupDeserializer();
    }

    @Bean
    ExperimentGroupSerializer experimentGroupSerializer() {
        return new ExperimentGroupSerializer();
    }
}
