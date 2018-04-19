package pl.allegro.experiments.chi.chiserver.infrastructure.experiments.converters;

import org.bson.Document;
import org.springframework.core.convert.converter.Converter;
import pl.allegro.experiments.chi.chiserver.domain.experiments.EventDefinition;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ReportingDefinition;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ReportingDefinitionDeserializer implements Converter<Document, ReportingDefinition> {
    private final EventDefinitionDeserializer eventDefinitionDeserializer;

    public ReportingDefinitionDeserializer(EventDefinitionDeserializer eventDefinitionDeserializer) {
        this.eventDefinitionDeserializer = eventDefinitionDeserializer;
    }

    @Override
    public ReportingDefinition convert(Document source) {
        List<EventDefinition> eventDefinitions = Optional.ofNullable(source.get("eventDefinitions"))
                .map(rawEventDefinitions -> ((List<Document>) rawEventDefinitions).stream()
                        .map(eventDefinitionDeserializer::convert)
                        .collect(Collectors.toList()))
                .orElse(Collections.emptyList());

        return new ReportingDefinition(
                eventDefinitions,
                (boolean) source.get("gtm"),
                (boolean) source.get("backendInteractionsEnabled")
        );
    }
}